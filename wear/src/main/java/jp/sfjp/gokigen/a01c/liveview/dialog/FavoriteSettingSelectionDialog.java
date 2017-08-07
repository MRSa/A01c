package jp.sfjp.gokigen.a01c.liveview.dialog;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Locale;

import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ICameraPropertyLoadSaveOperations;


/**
 *   お気に入り設定のダイアログ
 *
 *
 */
public class FavoriteSettingSelectionDialog implements IDialogDrawer
{
    private final ICameraPropertyLoadSaveOperations propertyOperation;
    private final IDialogDismissedNotifier dismissNotifier;
    private int selectedId = 0;
    private boolean isSaveOperation = false;  // loadはfalse, saveがtrue


    /**
     *   コンストラクタ
     *
     */
    public FavoriteSettingSelectionDialog(ICameraPropertyLoadSaveOperations operation, IDialogDismissedNotifier dismissNotifier)
    {
        this.propertyOperation = operation;
        this.dismissNotifier = dismissNotifier;
    }


    /**
     *   画面上にダイアログを表示する
     *
     *
     */
    @Override
    public void drawDialog(Canvas canvas)
    {
        final float WIDE_MARGIN = 10.0f;
        final float HEIGHT_MARGIN = 27.0f;
        final float STROKE_WIDTH = 2.0f;

        float width = canvas.getWidth();
        float height = canvas.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(WIDE_MARGIN, HEIGHT_MARGIN, (width - WIDE_MARGIN), (height - HEIGHT_MARGIN), paint);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawRect(WIDE_MARGIN, HEIGHT_MARGIN, (width - WIDE_MARGIN), (height - HEIGHT_MARGIN), paint);


    }

    /**
     *   画面でボタンが押された（押された位置 0.0f ～ 1.0f）
     *
     * @param posX  X座標 (左～右）
     * @param posY  Y座標（上～下）
     *
     * @return  true : ボタンなどがあるエリアだった / false : 何もしなかった
     */
    @Override
    public boolean touchedPosition(float posX, float posY)
    {
        if (dismissNotifier != null)
        {
            dismiss(false);
            return (true);
        }
        return (false);
    }


    /**
     *   プロパティの保存処理をする
     *
     * @param id  選択した番号
     */
    private void saveProperties(int id)
    {
        try
        {
            propertyOperation.saveProperties(String.format(Locale.ENGLISH, "%03d", id), "a01c:" + id);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   プロパティのロード処理をする
     *
     * @param id  選択した番号
     */
    private void loadProperties(int id)
    {
        try
        {
            propertyOperation.loadProperties(String.format(Locale.ENGLISH, "%03d", id), "a01c:" + id);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *   ダイアログを閉じる
     *
     * @param isExecuted  true: OK  / false: cancel
     */
    private void dismiss(boolean isExecuted)
    {
        dismissNotifier.dialogDismissed(isExecuted);
    }

}
