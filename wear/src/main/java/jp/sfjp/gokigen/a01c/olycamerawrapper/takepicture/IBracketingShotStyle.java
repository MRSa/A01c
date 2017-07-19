package jp.sfjp.gokigen.a01c.olycamerawrapper.takepicture;

/**
 *   IBracketingShotStyle :  ブラケッティング撮影処理の種類
 *
 * Created by MRSa on 2017/07/20.
 */
public interface IBracketingShotStyle
{
    int BRACKET_NONE = 0;       // 通常のショット
    int BRACKET_EXPREV = 1;     // 露出補正
    int BRACKET_APERTURE = 2;   // 絞り
    int BRACKET_ISO = 3;         // ISO
    int BRACKET_SHUTTER = 4;    // シャッター
    int BRACKET_WB = 5;          // ホワイトバランス
    int BRACKET_COLOR_TONE = 6; // カラートーン
}
