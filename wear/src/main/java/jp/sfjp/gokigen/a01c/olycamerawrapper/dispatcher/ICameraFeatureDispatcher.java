package jp.sfjp.gokigen.a01c.olycamerawrapper.dispatcher;

import android.view.MotionEvent;

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
    String ACTION_BUTTONL = "LA";

    // エリアアクション
    String ACTION_AREA1 = "A1";
    String ACTION_AREA2 = "A2";
    String ACTION_AREA3 = "A3";
    String ACTION_AREA4 = "A4";

    // ボタンラベル
    String DRAWABLE_BUTTON1 = "D1";
    String DRAWABLE_BUTTON2 = "D2";
    String DRAWABLE_BUTTON3 = "D3";
    String DRAWABLE_BUTTON4 = "D4";
    String DRAWABLE_BUTTON5 = "D5";
    String DRAWABLE_BUTTON6 = "D6";

    // 表示エリア
    String SHOW_TEXT_AREA_C = "TXTC";
    String SHOW_TEXT_AREA_1 = "TXT1";
    String SHOW_TEXT_AREA_2 = "TXT2";
    String SHOW_TEXT_AREA_3 = "TXT3";
    String SHOW_TEXT_AREA_4 = "TXT4";
    String SHOW_TEXT_AREA_5 = "TXT5";
    String SHOW_TEXT_AREA_6 = "TXT6";
    String SHOW_TEXT_AREA_7 = "TXT7";
    String SHOW_TEXT_AREA_8 = "TXT8";
    String SHOW_TEXT_AREA_9 = "TXT9";
    String SHOW_TEXT_AREA_A = "TXTA";

    String ACTION_SECOND_CHOICE = "_L_";

    // A01Cが持つ機能 (ボタンに割り当て可能な featureNumber)
    int FEATURE_ACTION_NONE = 0;
    int FEATURE_SETTINGS = 1;
    int FEATURE_TOGGLE_SHOW_GRID = 2;
    int FEATURE_SHUTTER_SINGLESHOT = 3;
    int FEATURE_CHANGE_TAKEMODE = 4;
    int FEATURE_CHAGE_AE_LOCK_MODE = 5;
    int FEATURE_EXPOSURE_BIAS_DOWN = 6;
    int FEATURE_EXPOSURE_BIAS_UP = 7;
    int FEATURE_APERTURE_DOWN = 8;
    int FEATURE_APERTURE_UP = 9;
    int FEATURE_SHUTTER_SPEED_DOWN = 10;
    int FEATURE_SHUTTER_SPEED_UP = 11;
    int FEATURE_COLORTONE_DOWN = 12;
    int FEATURE_COLORTONE_UP = 13;
    int FEATURE_ART_FILTER_DOWN = 14;
    int FEATURE_ART_FILTER_UP = 15;
    int FEATURE_TOGGLE_SHOW_LEVEL_GAUGE = 16;
    int FEATURE_CHANGE_TAKEMODE_REVERSE = 17;
    int FEATURE_CONTROL_MOVIE = 18;
    int FEATURE_AE_DOWN = 19;
    int FEATURE_AE_UP = 20;
    int FEATURE_ISO_DOWN = 21;
    int FEATURE_ISO_UP = 22;
    int FEATURE_WB_DOWN = 23;
    int FEATURE_WB_UP = 24;
    int FEATURE_QUALITY_MOVIE_DOWN = 25;
    int FEATURE_QUALITY_MOVIE_UP = 26;
    int FEATURE_SHORT_MOVIE_RECORD_TIME_DOWN = 27;
    int FEATURE_SHORT_MOVIE_RECORD_TIME_UP = 28;
    int FEATURE_EXPOSE_MOVIE_SELECT_DOWN = 29;
    int FEATURE_EXPOSE_MOVIE_SELECT_UP = 30;
    int FEATURE_CHANGE_AF_MF = 31;
    int FEATURE_CHANGE_AE = 32;
    int FEATURE_CHANGE_AE_REVERSE = 33;
    int FEATURE_SHOT_INTERVAL_3SEC = 34;
    int FEATURE_SHOT_INTERVAL_5SEC = 35;
    int FEATURE_SHOT_INTERVAL_10SEC = 36;
    int FEATURE_SHOT_BRACKET_EXPOSURE = 37;
    int FEATURE_SHOT_BRACKET_APERATURE = 38;
    int FEATURE_SHOT_BRACKET_SHUTTER = 39;
    int FEATURE_SHOT_BRACKET_COLORTONE = 40;
    int FEATURE_SHOT_BRACKET_WB = 41;
    int FEATURE_SHOT_BRACKET_ART_FILTER = 42;
    int FEATURE_SHOT_BRACKET_ISO = 43;
    int FEATURE_SHOW_FAVORITE_DIALOG = 44;
    int FEATURE_LENS_ZOOMIN = 45;
    int FEATURE_LENS_ZOOMOUT = 46;
    int FEATURE_LENS_ZOOMIN_2X = 47;
    int FEATURE_LENS_ZOOMOUT_2X = 48;
    int FEATURE_DIGITAL_ZOOM_RESET = 49;
    int FEATURE_DIGITAL_ZOOM_CHANGE = 50;
    int FEATURE_DIGITAL_ZOOMIN = 51;
    int FEATURE_DIGITAL_ZOOMOUT = 52;
    int FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X5 = 53;
    int FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X7 = 54;
    int FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X10 = 55;
    int FEATURE_CHANGE_LIVEVIEW_MAGNIFY_X14 = 56;


    // エリアタッチ時の機能(featureNumber)
    int FEATURE_AREA_ACTION_NONE = 100;
    int FEATURE_AREA_ACTION_NOT_CONNECTED = 101;
    int FEATURE_AREA_ACTION_DRIVE_AUTOFOCUS = 102;
    int FEATURE_AREA_ACTION_CHECK_CONTAINS_AUTOFOCUS_AREA = 103;



    // アクションインタフェース
    String getTakeMode();   // 撮影モードの取得
    boolean dispatchAction(int objectId, int featureNumber);  // コマンドの実行
    boolean dispatchAreaAction(MotionEvent event, int areaFeatureNumber);  // タッチエリアアクションの実行
}
