package jp.sfjp.gokigen.a01c.thetacamerawrapper.connection

import android.util.Log

class ThetaCameraDisconnectSequence : Runnable
{
    override fun run()
    {
        // カメラをPowerOffして接続を切る
        Log.v(ThetaCameraDisconnectSequence::class.java.simpleName, " Disconnect from THETA.")
    }
}
