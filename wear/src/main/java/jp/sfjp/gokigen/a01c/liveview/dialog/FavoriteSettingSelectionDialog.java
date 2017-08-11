package jp.sfjp.gokigen.a01c.liveview.dialog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.Locale;

import jp.sfjp.gokigen.a01c.olycamerawrapper.property.ICameraPropertyLoadSaveOperations;


/**
 *   お気に入り設定のダイアログ
 *
 *
 */
public class FavoriteSettingSelectionDialog implements IDialogDrawer
{
    private final String TAG = toString();
    private final ICameraPropertyLoadSaveOperations propertyOperation;
    private final IDialogDismissedNotifier dismissNotifier;
    private final Context context;
    private final float WIDE_MARGIN = 10.0f;
    private final float HEIGHT_MARGIN = 27.0f;
    private int selectedId = 0;
    private boolean isSaveOperation = false;  // loadはfalse, saveがtrue



    /**
     *   コンストラクタ
     *
     */
    public FavoriteSettingSelectionDialog(Context context, ICameraPropertyLoadSaveOperations operation, IDialogDismissedNotifier dismissNotifier)
    {
        this.context = context;
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

        final float STROKE_WIDTH = 2.0f;

        float width = canvas.getWidth();
        float height = canvas.getHeight();

        float height_unit = (height - 2.0f * (HEIGHT_MARGIN)) / 20.0f;
        float width_unit = (width - 2.0f * (WIDE_MARGIN)) / 21.0f;

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // 背景を消す
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(WIDE_MARGIN, HEIGHT_MARGIN, (width - WIDE_MARGIN), (height - HEIGHT_MARGIN), paint);

        // 外枠を引く
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawRect(WIDE_MARGIN, HEIGHT_MARGIN, (width - WIDE_MARGIN), (height - HEIGHT_MARGIN), paint);
        canvas.drawLine(WIDE_MARGIN, ((height - HEIGHT_MARGIN) - (height_unit * 4.0f)), (width - WIDE_MARGIN),((height - HEIGHT_MARGIN) - (height_unit * 4.0f)), paint);
        canvas.drawLine((width / 2.0f), ((height - HEIGHT_MARGIN) - (height_unit * 4.0f)), (width / 2.0f), (height - HEIGHT_MARGIN), paint);
        canvas.drawLine(WIDE_MARGIN, (HEIGHT_MARGIN + (height_unit * 3.0f)), (width - WIDE_MARGIN),  (HEIGHT_MARGIN + (height_unit * 3.0f)), paint);

        Paint.Style style;

        // Load
        style = (isSaveOperation) ? Paint.Style.STROKE : Paint.Style.FILL;
        paint.setStyle(style);
        canvas.drawRect((WIDE_MARGIN + (width_unit * 1.0f)), HEIGHT_MARGIN + 4.0f * height_unit, (WIDE_MARGIN + width_unit * (1.0f + 9.0f)), HEIGHT_MARGIN + 8.0f * height_unit, paint);

        // Save
        style = (!isSaveOperation) ? Paint.Style.STROKE : Paint.Style.FILL;
        paint.setStyle(style);
        canvas.drawRect((WIDE_MARGIN + (width_unit * 11.0f)), HEIGHT_MARGIN + 4.0f * height_unit, (WIDE_MARGIN + width_unit * (11.0f + 9.0f)), HEIGHT_MARGIN + 8.0f * height_unit, paint);

        // ボタン０
        style = (selectedId != 0) ? Paint.Style.STROKE : Paint.Style.FILL;
        paint.setStyle(style);
        canvas.drawRect((WIDE_MARGIN + (width_unit * 1.0f)), HEIGHT_MARGIN + 9.0f * height_unit, (WIDE_MARGIN + width_unit * (1.0f + 3.0f)), HEIGHT_MARGIN + 15.0f * height_unit, paint);

        // ボタン１
        style = (selectedId != 1) ? Paint.Style.STROKE : Paint.Style.FILL;
        paint.setStyle(style);
        canvas.drawRect((WIDE_MARGIN + (width_unit * 5.0f)), HEIGHT_MARGIN + 9.0f * height_unit, (WIDE_MARGIN + width_unit * (5.0f + 3.0f)), HEIGHT_MARGIN + 15.0f * height_unit, paint);

        // ボタン２
        style = (selectedId != 2) ? Paint.Style.STROKE : Paint.Style.FILL;
        paint.setStyle(style);
        canvas.drawRect((WIDE_MARGIN + (width_unit * 9.0f)), HEIGHT_MARGIN + 9.0f * height_unit, (WIDE_MARGIN + width_unit * (9.0f + 3.0f)), HEIGHT_MARGIN + 15.0f * height_unit, paint);

        // ボタン３
        style = (selectedId != 3) ? Paint.Style.STROKE : Paint.Style.FILL;
        paint.setStyle(style);
        canvas.drawRect((WIDE_MARGIN + (width_unit * 13.0f)), HEIGHT_MARGIN + 9.0f * height_unit, (WIDE_MARGIN + width_unit * (13.0f + 3.0f)), HEIGHT_MARGIN + 15.0f * height_unit, paint);

        // ボタン４
        style = (selectedId != 4) ? Paint.Style.STROKE : Paint.Style.FILL;
        paint.setStyle(style);
        canvas.drawRect((WIDE_MARGIN + (width_unit * 17.0f)), HEIGHT_MARGIN + 9.0f * height_unit, (WIDE_MARGIN + width_unit * (17.0f + 3.0f)), HEIGHT_MARGIN + 15.0f * height_unit, paint);

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
        Log.v(TAG, " FavoriteSettingSelectionDialog::touchedPosition()  [" + posX + "," + posY + "]");

        //  押された場所をチェックする
        if (posY > (16.0f / 20.0f))
        {
            // 画面下部のOK or Cancelが押された
            dismiss(posX);
            return (true);
        }
        if ((posY >= (4.0f / 20.0f))&&(posY <= (8.0f / 20.0f)))
        {
            // Load/Saveボタンの領域が押された
            isSaveOperation = (posX >= (10.0f / 21.0f));
            return (true);
        }
        if ((posY >= (9.0f / 20.0f))&&(posY <= (15.0f / 20.0f)))
        {
            // IDボタンの領域が押された
            selectedIdArea(posX);
            return (true);
        }
        return (false);
    }

    /**
     *  選択したIDボタン...
     *
     */
    private void selectedIdArea(float posX)
    {
        if (posX <= (4.0f / 21.0f))
        {
            selectedId = 0;
        }
        else if (posX <= ((4.0f * 2.0f) / 21.0f))
        {
            selectedId = 1;
        }
        else if (posX <= ((4.0f * 3.0f) / 21.0f))
        {
            selectedId = 2;
        }
        else if (posX <= ((4.0f * 4.0f) / 21.0f))
        {
            selectedId = 3;
        }
        else //if (posX <= ((4.0f * 5.0f) / 21.0f))
        {
            selectedId = 4;
        }
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
     * @param posX ： ボタンが押された位置
     */
    private void dismiss(float posX)
    {
        boolean isExecute = (posX > 0.5f);
        if (isExecute)
        {
            Log.v(TAG, "  EXECUTE OPERATION  : " + selectedId + " " + isSaveOperation);
/**/
            // コマンドを実行する
            if (isSaveOperation)
            {
                // プロパティの保存
                saveProperties(selectedId + 1);
            }
            else
            {
                // プロパティの読み出し
                loadProperties(selectedId + 1);
            }
/**/
        }
        if (dismissNotifier != null)
        {
            dismissNotifier.dialogDismissed(isExecute);
        }
    }
}