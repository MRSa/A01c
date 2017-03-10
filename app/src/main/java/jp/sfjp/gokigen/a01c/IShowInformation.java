package jp.sfjp.gokigen.a01c;

/**
 *
 */
public interface IShowInformation
{
    int AREA_C = 0;
    int AREA_1 = 1;
    int AREA_2 = 2;
    int AREA_3 = 3;
    int AREA_4 = 4;
    int AREA_5 = 5;
    int AREA_6 = 6;

    int BUTTON_1 = 1;
    int BUTTON_2 = 2;
    int BUTTON_3 = 3;
    int BUTTON_4 = 4;
    int BUTTON_5 = 5;
    int BUTTON_6 = 6;

    void setMessage(final int area, final int color, final String message);
    void setButtonDrawable(final int button, final int labelId);

}
