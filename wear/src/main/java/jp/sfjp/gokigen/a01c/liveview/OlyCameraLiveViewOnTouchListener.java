package jp.sfjp.gokigen.a01c.liveview;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import jp.sfjp.gokigen.a01c.IChangeScene;
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
    private boolean prohibitOperation = true;
    private boolean suppressOperation = false;


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
        if (prohibitOperation)
        {
            // 操作禁止の指示がされていた場合は、、接続機能を呼び出す
            Log.v(TAG, "onClick() : prohibit operation");
            if (!suppressOperation)
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
        if (prohibitOperation)
        {
            // 操作禁止の指示がされていた場合は何もしない
            Log.v(TAG, "onLongClick() : prohibit operation");
            /*
            if (!suppressOperation)
            {
                return (changeScene.checkConnectionFeature(1));
            }
            return (false);
            */
            return  ((!suppressOperation)&&(changeScene.checkConnectionFeature(1)));
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
        if (prohibitOperation)
        {
            // 操作禁止の指示がされていた場合は、接続状態を示すようにする
            Log.v(TAG, "onTouch() : prohibit operation");
            return ((!suppressOperation)&&(changeScene.showConnectionStatus()));
        }
        // 現在のところ、タッチエリアの場合はオートフォーカス実行で固定
        return ((id == R.id.liveview)&&(dispatcher.dispatchAreaAction(event, ICameraFeatureDispatcher.FEATURE_AREA_ACTION_DRIVE_AUTOFOCUS)));
    }

    /**
     *   操作の可否を設定する。
     *
     *    @param operation  true: 操作可能, false: 操作不可
     *    @param suppress   true: 操作不可, false: 接続操作実施 （操作不可時のコマンド受付）
     */
    public void setEnableOperation(boolean operation, boolean suppress)
    {
        prohibitOperation = !operation;
        suppressOperation = suppress;
    }

    /**
     *   操作可能状態かを応答する。
     *
     * @return true: 操作可能, false: 操作不可
     */
    public boolean isEnabledOperation()
    {
        return (!prohibitOperation);
    }

}
