package jp.sfjp.gokigen.a01c.olycamerawrapper.property;

public interface ICameraPropertyLoadSaveOperations
{
    void saveProperties(final String idHeader, final String dataName);
    void loadProperties(final String idHeader, final String dataName);
}
