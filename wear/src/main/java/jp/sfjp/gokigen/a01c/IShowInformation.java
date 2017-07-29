package jp.sfjp.gokigen.a01c;

/**
 *
 */
public interface IShowInformation
{
    int AREA_NONE = 0;
    int AREA_C = 1;
    int AREA_1 = 2;
    int AREA_2 = 3;
    int AREA_3 = 4;
    int AREA_4 = 5;
    int AREA_5 = 6;
    int AREA_6 = 7;
    int AREA_7 = 8;
    int AREA_8 = 9;
    int AREA_9 = 10;
    int AREA_A = 11;

    int BUTTON_1 = 1;
    int BUTTON_2 = 2;
    int BUTTON_3 = 3;
    int BUTTON_4 = 4;
    int BUTTON_5 = 5;
    int BUTTON_6 = 6;

    int VIBRATE_PATTERN_NONE  = 0;
    int VIBRATE_PATTERN_SIMPLE_SHORT  = 1;
    int VIBRATE_PATTERN_SIMPLE_MIDDLE  = 2;
    int VIBRATE_PATTERN_SIMPLE_LONG = 3;
    int VIBRATE_PATTERN_SIMPLE_LONGLONG = 4;
    int VIBRATE_PATTERN_SHORT_DOUBLE  = 5;

    void setMessage(final int area, final int color, final String message);
    void setButtonDrawable(final int button, final int labelId);

    void vibrate(final int vibratePattern);

    void setEnabledOperation(boolean operation, boolean suppress);

    void showFavoriteSettingsDialog();
}
