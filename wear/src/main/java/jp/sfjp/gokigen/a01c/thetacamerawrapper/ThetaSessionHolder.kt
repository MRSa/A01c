package jp.sfjp.gokigen.a01c.thetacamerawrapper

import android.util.Log

class ThetaSessionHolder : IThetaSessionIdProvider, IThetaSessionIdNotifier
{
    private var sessionId : String = ""

    override fun getSessionId(): String
    {
        return (sessionId)
    }

    override fun receivedSessionId(sessionId: String?)
    {
        if (!(sessionId.isNullOrEmpty()))
        {
            Log.v(TAG, " SESSION ID : $sessionId")
            this.sessionId = sessionId
        }
    }

    fun isApiLevelV21() : Boolean
    {
        return (sessionId.isEmpty())
    }

    companion object
    {
        private val TAG = ThetaSessionHolder::class.java.simpleName
    }

}
