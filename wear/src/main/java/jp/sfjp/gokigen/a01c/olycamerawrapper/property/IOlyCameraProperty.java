package jp.sfjp.gokigen.a01c.olycamerawrapper.property;

/**
 *   使用する（かもしれない）カメラプロパティのキー一覧
 *
 *
 */
public interface IOlyCameraProperty
{
    String TAKE_MODE = "TAKEMODE";
    String DRIVE_MODE = "TAKE_DRIVE";
    String WB_MODE = "WB";
    String BATTERY_LEVEL = "BATTERY_LEVEL";
    String AE_MODE = "AE";
    String AE_LOCK_STATE = "AE_LOCK_STATE";
    String AF_LOCK_STATE = "AF_LOCK_STATE";

    String ISO_SENSITIVITY = "ISO";

    String QUALITY_MOVIE = "QUALITY_MOVIE";
    String SHORT_MOVIE_RECORD_TIME = "QUALITY_MOVIE_SHORT_MOVIE_RECORD_TIME";
    String TAKE_MODE_MOVIE = "EXPOSE_MOVIE_SELECT";

    String EXPOSURE_COMPENSATION = "EXPREV";
    String SHUTTER_SPEED = "SHUTTER";
    String APERTURE ="APERTURE";

    String SOUND_VOLUME_LEVEL = "SOUND_VOLUME_LEVEL";
    String RAW = "RAW";
    String TAKE_DRIVE = "TAKE_DRIVE";
    String CONTINUOUS_SHOOTING_VELOCITY = "CONTINUOUS_SHOOTING_VELOCITY";

    String COLOR_PHASE = "COLOR_PHASE";
    String COLOR_CREATOR_COLOR = "COLOR_CREATOR_COLOR";
    String COLOR_CREATOR_VIVID = "COLOR_CREATOR_VIVID";

    String TONE_CONTROL_LOW = "TONE_CONTROL_LOW";
    String TONE_CONTROL_MIDDLE = "TONE_CONTROL_MIDDLE";
    String TONE_CONTROL_HIGH = "TONE_CONTROL_HIGH";

    String MONOTONEFILTER_MONOCHROME = "MONOTONEFILTER_MONOCHROME";
    String MONOTONEFILTER_ROUGH_MONOCHROME = "MONOTONEFILTER_ROUGH_MONOCHROME";
    String MONOTONEFILTER_DRAMATIC_TONE = "MONOTONEFILTER_DRAMATIC_TONE";

    String MONOTONECOLOR_MONOCHROME = "MONOTONECOLOR_MONOCHROME";
    String MONOTONECOLOR_ROUGH_MONOCHROME = "MONOTONECOLOR_ROUGH_MONOCHROME";
    String MONOTONECOLOR_DRAMATIC_TONE = "MONOTONECOLOR_DRAMATIC_TONE";

    String FULL_TIME_AF = "FULL_TIME_AF";
    String FOCUS_MOVIE = "FOCUS_MOVIE";

    String FOCUS_STILL = "FOCUS_STILL";

    String FOCUS_MF = "FOCUS_MF";
    String FOCUS_SAF = "FOCUS_SAF";

    String REC_PREVIEW = "RECVIEW";

    String COLOR_TONE = "COLORTONE";
    String ART_FILTER = "RECENTLY_ART_FILTER";

}
