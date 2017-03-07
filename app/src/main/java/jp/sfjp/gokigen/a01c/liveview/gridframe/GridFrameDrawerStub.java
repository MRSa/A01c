package jp.sfjp.gokigen.a01c.liveview.gridframe;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class GridFrameDrawerStub  implements IGridFrameDrawer
{

    @Override
    public void drawFramingGrid(Canvas canvas, RectF rect, Paint paint)
    {

    }

    @Override
    public int getDrawColor()
    {
        return (Color.argb(130,235,235,235));
    }
}
