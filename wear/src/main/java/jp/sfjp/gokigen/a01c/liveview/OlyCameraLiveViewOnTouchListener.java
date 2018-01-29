package jp.sfjp.gokigen.a01c.liveview;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import jp.sfjp.gokigen.a01c.IChangeScene;
import jp.sfjp.gokigen.a01c.IShowInformation;
import jp.sfjp.gokigen.a01c.R;
import jp.sfjp.gokigen.a01c.liveview.button.IPushedButton;
import jp.sfjp.gokigen.a01c.liveview.button.PushedButtonFactory;
import jp.sfjp.gokigen.a01c.olycamerawrapper.dispatcher.ICameraFeatureDispatcher;

/**
 *   画面がタッチ・クリックされた時の処理分岐
 *
 */
public class OlyCameraLiveViewOnTouchListener  implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener
{
    private final String TAG = toString();

    private final ICameraFeatureDispatcher dispatcher;
    private final IChangeScene changeScene;
    private final SparseArray<IPushedButton> buttonDispatcher;
    private IShowInformation.operation operationMode = IShowInformation.operation.ONLY_CONNECT;

    /**
     *   コンストラクタの整理
     *
     */
    public OlyCameraLiveViewOnTouchListener(Context context, ICameraFeatureDispatcher dispatcher, IChangeScene changeScene)
    {
        this.dispatcher = dispatcher;
        this.changeScene = changeScene;
        buttonDispatcher = new PushedButtonFactory(context, dispatcher).getButtonMap();
    }

    /**
     *   ボタン（オブジェクト）をクリックしたときの処理
     */
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        Log.v(TAG, "onClick() : " + id);
        if (operationMode != IShowInformation.operation.ENABLE)
        {
            // 操作禁止の指示がされていた場合は、、接続機能を呼び出す
            Log.v(TAG, "onClick() : prohibit operation");
            if (operationMode == IShowInformation.operation.ONLY_CONNECT)
            {
                changeScene.checkConnectionFeature(0);
            }
            return;
        }
        try
        {
            IPushedButton button = buttonDispatcher.get(id);
            if (button != null)
            {
                button.pushedButton(false);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   長押しされたとき...
     *
     *
     */
    @Override
    public boolean onLongClick(View v)
    {
        boolean ret = false;
        int id = v.getId();
        Log.v(TAG, "onLongClick() : " + id);
        if (operationMode != IShowInformation.operation.ENABLE)
        {
            // 操作禁止の指示がされていた場合は何もしない
            Log.v(TAG, "onLongClick() : prohibit operation");
            return  ((operationMode == IShowInformation.operation.ONLY_CONNECT)&&(changeScene.checkConnectionFeature(1)));
        }
        try
        {
            IPushedButton button = buttonDispatcher.get(id);
            if (button != null)
            {
                ret = button.pushedButton(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (ret);
    }

    /**
     *   画面(ライブビュー部分)をタッチした時の処理
     *
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int id = v.getId();
        Log.v(TAG, "onTouch() : " + id);
        if (operationMode != IShowInformation.operation.ENABLE)
        {
            if (operationMode == IShowInformation.operation.ENABLE_ONLY_TOUCHED_POSITION)
            {
                Log.v(TAG, "touchedPosition() : [" + event.getX() + "," + event.getY() + "](" +  v.getWidth() +"," + v.getHeight() + ")");
                return (changeScene.touchedPosition((event.getX() / v.getWidth()), (event.getY() / v.getHeight())));
            }
            // 操作禁止の指示がされていた場合は、接続状態を示すようにする
            Log.v(TAG, "onTouch() : prohibit operation");
            return ((operationMode == IShowInformation.operation.ONLY_CONNECT)&&(changeScene.showConnectionStatus()));
        }

        // 画面下部のエリア（オートフォーカスエリア外）をタッチした場合には、ボタン押下アクションに切り替える
        int hookId = checkHookTouchedPosition(v, event);
        if (hookId != 0)
        {
            boolean ret = false;
            if (hookId == R.id.liveview)
            {
                //  何もしないパターン。。。
                return (true);
            }
            try
            {
                IPushedButton button = buttonDispatcher.get(hookId);
                if (button != null)
                {
                    // ボタンを押したことにする
                    ret = button.pushedButton(false);
                    //v.performClick();  // 本来はこっちで動かしたい。
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return (ret);
        }

        // オートフォーカスエリアに含まれていた場合には、オートフォーカスを実行する
        return ((id == R.id.liveview)&&(dispatcher.dispatchAreaAction(event, ICameraFeatureDispatcher.FEATURE_AREA_ACTION_DRIVE_AUTOFOCUS)));
    }

    /**
     *　  タッチエリアを確認しフックするかどうか確認する (0なら hook しない)
     *
     */
    private int checkHookTouchedPosition(View v, MotionEvent event)
    {
        int hookId = 0;
        try
        {
            // オートフォーカスエリア内かどうかチェックする
            if (dispatcher.dispatchAreaAction(event, ICameraFeatureDispatcher.FEATURE_AREA_ACTION_CHECK_CONTAINS_AUTOFOCUS_AREA))
            {
                if (event.getAction() != MotionEvent.ACTION_DOWN)
                {
                    // オートフォーカスエリア内のときには、ACTION_DOWN のみを拾う
                    return (R.id.liveview);
                }
                // オートフォーカスエリアに含まれているのでオートフォーカスする
                return (0);
            }
            if (event.getAction() != MotionEvent.ACTION_UP)
            {
                // オートフォーカスエリア外のときには、 ACTION_UP のみを拾う
                return (R.id.liveview);
            }

            // オートフォーカスエリア外なので、イベントをフックしてボタン操作に変える（当面は右下のみ）
            float areaY = event.getY() / v.getHeight();
            float areaX = event.getX() / v.getWidth();
            Log.v(TAG, "HOOKED POSITION (areaX : " + areaX + " areaY : " + areaY + ")");
            if (areaY > 0.66f)
            {
                if (areaX > 0.8333f)
                {
                    // 画面右下のオートフォーカスエリア外のときのみ、撮影ボタンを押したことにする
                    // (0.66f ... 画面タッチエリアの下 1/3、0.8333f ... 画面タッチエリアの右側 1/6)
                    return (R.id.btn_6);
                }
                else if (areaX < 0.1666f)
                {
                    return (R.id.btn_1);
                }
            }
        }
        catch (Exception e)
        {
            // ちゃんとポジションが取れなかった...
            e.printStackTrace();
            hookId = 0;
        }
        return (hookId);
    }

    /**
     *   操作の可否を設定する。
     *
     */
    public void setEnableOperation(IShowInformation.operation requestOperation)
    {
        operationMode = requestOperation;
    }

    /**
     *   操作可能状態かを応答する。
     *
     */
    public IShowInformation.operation isEnabledOperation()
    {
        return (operationMode);
    }

}
