package jp.sfjp.gokigen.a01c.liveview;

import android.graphics.Color;

/**
 *
 *
 * Created by MRSa on 2017/03/01.
 */
public class ShowMessageHolder
{
    private String upperMessage = "";
    private String centerMessage = "";
    private String lowerMessage = "";

    private int upperMessageColor = Color.BLUE;
    private int centerMessageColor = Color.BLUE;
    private int lowerMessageColor = Color.BLUE;

    private int upperMessageTextSize = 8;
    private int centerMessageTextSize = 16;
    private int lowerMessageTextSize = 8;

    // フォーカスフレームの状態
    enum MessageArea
    {
        UP,
        CENTER,
        LOW
    };

    /**
     *
     *
     */
    public void setMessageToShow(MessageArea area, int color, int size, String message)
    {
        switch (area)
        {
            case CENTER:
                centerMessageColor = color;
                centerMessageTextSize = size;
                centerMessage = message;
                break;

            case UP:
                upperMessageColor = color;
                upperMessageTextSize = size;
                upperMessage = message;
                break;

            case LOW:
            default:
                lowerMessageColor = color;
                lowerMessageTextSize = size;
                lowerMessage = message;
                break;
        }
    }

    /**
     *
     *
     */
    int getSize(MessageArea area)
    {
        int size;
        switch (area)
        {
            case CENTER:
                size = centerMessageTextSize;
                break;

            case UP:
                size = upperMessageTextSize;
                break;

            case LOW:
            default:
                size = lowerMessageTextSize;
                break;
        }
        return (size);
    }

    /**
     *
     *
     */
    int getColor(MessageArea area)
    {
        int color;
        switch (area)
        {
            case CENTER:
                color = centerMessageColor;
                break;

            case UP:
                color = upperMessageColor;
                break;

            case LOW:
            default:
                color = lowerMessageColor;
                break;
        }
        return (color);
    }

    /**
     *
     *
     */
    String getMessage(MessageArea area)
    {
        String message;
        switch (area)
        {
            case CENTER:
                message = centerMessage;
                break;

            case UP:
                message = upperMessage;
                break;

            case LOW:
            default:
                message = lowerMessage;
                break;
        }
        return (message);
    }
}
