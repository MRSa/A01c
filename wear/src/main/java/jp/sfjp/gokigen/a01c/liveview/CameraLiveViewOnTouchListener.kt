package jp.sfjp.gokigen.a01c.liveview

import android.content.Context
import android.util.Log
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import jp.sfjp.gokigen.a01c.ICameraFeatureDispatcher
import jp.sfjp.gokigen.a01c.IChangeScene
import jp.sfjp.gokigen.a01c.IShowInformation.operation
import jp.sfjp.gokigen.a01c.R
import jp.sfjp.gokigen.a01c.liveview.button.IPushedButton
import jp.sfjp.gokigen.a01c.liveview.button.PushedButtonFactory

// 画面がタッチ・クリックされた時の処理分岐
class CameraLiveViewOnTouchListener(
    context: Context?,
    private val dispatcher: ICameraFeatureDispatcher,
    private val changeScene: IChangeScene
) : View.OnClickListener, OnTouchListener, OnLongClickListener
{
    private val buttonDispatcher: SparseArray<IPushedButton?> =
        PushedButtonFactory(context, dispatcher).buttonMap
    private var operationMode: operation? = operation.ONLY_CONNECT

    // ボタン（オブジェクト）をクリックしたときの処理
    override fun onClick(v: View) {
        val id = v.id
        Log.v(TAG, "onClick() : $id")
        if (operationMode != operation.ENABLE) {
            // 操作禁止の指示がされていた場合は、、接続機能を呼び出す
            Log.v(TAG, "onClick() : prohibit operation")
            if (operationMode == operation.ONLY_CONNECT) {
                changeScene.checkConnectionFeature(0, id)
            }
            return
        }
        try {
            val button = buttonDispatcher.get(id)
            button?.pushedButton(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 長押しされたとき...
    override fun onLongClick(v: View): Boolean
    {
        var ret = false
        val id = v.id
        Log.v(TAG, "onLongClick() : $id")
        if (operationMode != operation.ENABLE) {
            // 操作禁止の指示がされていた場合は何もしない
            Log.v(TAG, "onLongClick() : prohibit operation")
            return ((operationMode == operation.ONLY_CONNECT) && (changeScene.checkConnectionFeature(
                1,
                id
            )))
        }
        try {
            val button = buttonDispatcher.get(id)
            if (button != null) {
                ret = button.pushedButton(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return (ret)
    }

    // 画面(ライブビュー部分)をタッチした時の処理
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val id = v.id
        Log.v(TAG, "onTouch() : $id")
        if (operationMode != operation.ENABLE) {
            if (operationMode == operation.ENABLE_ONLY_TOUCHED_POSITION) {
                Log.v(
                    TAG,
                    "touchedPosition() : [${event.x},${event.y}](${v.width},${v.height})"
                )
                return (changeScene.touchedPosition(
                    (event.x / v.width),
                    (event.y / v.height)
                ))
            }
            // 操作禁止の指示がされていた場合は、接続状態を示すようにする
            Log.v(TAG, "onTouch() : prohibit operation")
            return ((operationMode == operation.ONLY_CONNECT) && (changeScene.showConnectionStatus()))
        }

        // 画面下部のエリア（オートフォーカスエリア外）をタッチした場合には、ボタン押下アクションに切り替える
        val hookId = checkHookTouchedPosition(v, event)
        if (hookId != 0) {
            var ret = false
            if (hookId == R.id.button_area1) {
                //  何もしないパターン。。。
                return (true)
            }
            try {
                val button = buttonDispatcher.get(R.id.liveview)
                if (button != null) {
                    // ボタンを押したことにする
                    ret = if (hookId == R.id.btn_1) {
                        // 左側のエリア → 長押し
                        button.pushedButton(true)
                    } else  // (hookId == R.id.btn_6)
                    {
                        // 右側のエリア → クリック
                        button.pushedButton(false)
                    }
                    //v.performClick();  // 本来はこっちで動かしたい。
                }
            } catch (e: Exception) {
                e.printStackTrace()
                v.performClick() // exception のときだけ...ダミー処理
            }
            return (ret)
        }

        // オートフォーカスエリアに含まれていた場合には、オートフォーカスを実行する
        return ((id == R.id.liveview) && (dispatcher.dispatchAreaAction(
            event,
            ICameraFeatureDispatcher.FEATURE_AREA_ACTION_DRIVE_AUTOFOCUS
        )))
    }

     // 　  タッチエリアを確認しフックするかどうか確認する
     // (0なら hook しない, 左のエリアは、R.id.btn_1, 右のエリアは、R.id.btn_2, 何もしない場合は R.id.area)
    private fun checkHookTouchedPosition(v: View, event: MotionEvent): Int {
        try {
            // オートフォーカスエリア内かどうかチェックする
            if (dispatcher.dispatchAreaAction(
                    event,
                    ICameraFeatureDispatcher.FEATURE_AREA_ACTION_CHECK_CONTAINS_AUTOFOCUS_AREA
                )
            ) {
                if (event.action != MotionEvent.ACTION_DOWN) {
                    // オートフォーカスエリア内のときには、ACTION_DOWN のみを拾う
                    return (R.id.button_area1)
                }
                // オートフォーカスエリアに含まれているのでオートフォーカスする
                return (0)
            }
            if (event.action != MotionEvent.ACTION_UP) {
                // オートフォーカスエリア外のときには、 ACTION_UP のみを拾う
                return (R.id.button_area1)
            }

            // オートフォーカスエリア外なので、イベントをフックしてボタン操作に変える（当面は右下のみ）
            val areaY = event.y / v.height
            val areaX = event.x / v.width
            Log.v(TAG, "HOOKED POSITION (areaX : $areaX areaY : $areaY)")
            if (areaY > 0.70f) {
                if (areaX > 0.8333f) {
                    // 画面右下のオートフォーカスエリア外のときのみ、撮影ボタンを押したことにする
                    // (0.66f ... 画面タッチエリアの下 1/3、0.8333f ... 画面タッチエリアの右側 1/6)
                    return (R.id.btn_6)
                } else if (areaX < 0.1666f) {
                    return (R.id.btn_1)
                }
            }
        } catch (e: Exception) {
            // ちゃんとポジションが取れなかった...
            e.printStackTrace()
        }
        // エリア外だけれどもオートフォーカスする
        return (0)
    }

    // 操作の可否を設定する。
    fun setEnableOperation(requestOperation: operation?) {
        operationMode = requestOperation
    }

    val isEnabledOperation: operation?
        // 操作可能状態かを応答する。
        get() = (operationMode)

    companion object {
        private val TAG = CameraLiveViewOnTouchListener::class.java.simpleName
    }
}
