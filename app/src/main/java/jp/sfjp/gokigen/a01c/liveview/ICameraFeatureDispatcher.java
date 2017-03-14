package jp.sfjp.gokigen.a01c.liveview;

/**
 *   機能とボタンの設定群
 *
 */
public interface ICameraFeatureDispatcher
{
    // 撮影モード
    String MODE_P = "_P";
    String MODE_M = "_M";
    String MODE_A = "_A";
    String MODE_S = "_S";
    String MODE_ART = "_ART";
    String MODE_IAUTO = "_iAUTO";
    String MODE_MOVIE = "_MOVIE";

    // ボタンアクション
    String ACTION_BUTTON1 = "B1";
    String ACTION_BUTTON2 = "B2";
    String ACTION_BUTTON3 = "B3";
    String ACTION_BUTTON4 = "B4";
    String ACTION_BUTTON5 = "B5";
    String ACTION_BUTTON6 = "B6";

    // エリアアクション
    String ACTION_AREA1 = "A1";
    String ACTION_AREA2 = "A2";
    String ACTION_AREA3 = "A3";

    // ボタンラベル
    String DRAWABLE_BUTTON1 = "D1";
    String DRAWABLE_BUTTON2 = "D2";
    String DRAWABLE_BUTTON3 = "D3";
    String DRAWABLE_BUTTON4 = "D4";
    String DRAWABLE_BUTTON5 = "D5";
    String DRAWABLE_BUTTON6 = "D6";

    // 表示エリア
    String SHOW_TEXT_AREA1 = "TXT1";
    String SHOW_TEXT_AREA2 = "TXT2";
    String SHOW_TEXT_AREA3 = "TXT3";
    String SHOW_TEXT_AREA4 = "TXT4";
    String SHOW_TEXT_AREA5 = "TXT5";
    String SHOW_TEXT_AREA6 = "TXT6";
    String SHOW_TEXT_AREA7 = "TXT7";


    // A01Cが持つ機能 (ボタンに割り当て可能)
    int FEATURE_ACTION_NONE = 0;
    int FEATURE_SETTINGS = 1;
    int FEATURE_TOGGLE_SHOW_GRID = 2;
    int FEATURE_SHUTTER_SINGLESHOT = 3;
    int FEATURE_CHANGE_TAKEMODE = 4;
    int FEATURE_CHAGE_AE_LOCK_MODE = 5;

}
