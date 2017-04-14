package jp.sfjp.gokigen.a01c.liveview;

import android.graphics.Color;

import jp.sfjp.gokigen.a01c.olycamerawrapper.ILevelGauge;

/**
 *
 *
 */
class ShowMessageHolder implements IMessageDrawer
{
    /**
     *
     */
    private class messageHolder
    {
        private String message = "";
        private int color = Color.BLUE;
        private int textSize = 16;

        String getMessage()
        {
            return message;
        }

        void setMessage(String message)
        {
            this.message = message;
        }

        int getColor()
        {
            return color;
        }

        void setColor(int color)
        {
            this.color = color;
        }

        int getTextSize()
        {
            return textSize;
        }

        void setTextSize(int textSize)
        {
            this.textSize = textSize;
        }
    }

    private messageHolder upperLeft = new messageHolder();
    private messageHolder upperRight = new messageHolder();
    private messageHolder upperCenter = new messageHolder();
    private messageHolder center = new messageHolder();
    private messageHolder lowerLeft = new messageHolder();
    private messageHolder lowerRight = new messageHolder();
    private messageHolder lowerCenter = new messageHolder();

    private ILevelGauge levelGauge = null;

    /**
     *   コンストラクタ
     *
     */
    ShowMessageHolder()
    {
        center.setTextSize(24);
    }

    /**
     *
     *
     */
    private messageHolder decideHolder(MessageArea area)
    {
        messageHolder target;
        switch (area)
        {
            case CENTER:
                target = center;
                break;

            case UPLEFT:
                target = upperLeft;
                break;

            case UPRIGHT:
                target = upperRight;
                break;

            case UPCENTER:
                target = upperCenter;
                break;

            case LOWLEFT:
                target = lowerLeft;
                break;

            case LOWCENTER:
                target = lowerCenter;
                break;

            case LOWRIGHT:
            default:
                target = lowerRight;
                break;
        }
        return (target);
    }

    /**
     *
     *
     */
    @Override
    public void setMessageToShow(MessageArea area, int color, int size, String message)
    {
        messageHolder target = decideHolder(area);
        target.setColor(color);
        target.setTextSize(size);
        target.setMessage(message);
    }

    /**
     *
     */
    @Override
    public void setLevelGauge(ILevelGauge levelGauge)
    {
        this.levelGauge = levelGauge;
    }

    /**
     *
     */
    @Override
    public ILevelGauge getLevelGauge()
    {
        return (levelGauge);
    }

    /**
     *
     *
     */
    int getSize(MessageArea area)
    {
        messageHolder target = decideHolder(area);
        return (target.getTextSize());
    }

    /**
     *
     *
     */
    int getColor(MessageArea area)
    {
        messageHolder target = decideHolder(area);
        return (target.getColor());
    }

    /**
     *
     *
     */
    String getMessage(MessageArea area)
    {
        messageHolder target = decideHolder(area);
        return (target.getMessage());
    }



}
