package jp.sfjp.gokigen.a01c.thetacamerawrapper

class ThetaSessionHolder : IThetaSessionIdProvider, IThetaSessionIdNotifier
{
    private var sessionId : String = ""

    override fun getSessionId(): String
    {
        return (sessionId)
    }

    override fun receivedSessionId(sessionId: String?)
    {
        if (sessionId != null)
        {
            this.sessionId = sessionId
        }
    }

    fun isApiLevelV21() : Boolean
    {
        return (!(sessionId.isNotEmpty()))
    }
}
