package jp.sfjp.gokigen.a01c.olycamerawrapper.property;

import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.olympus.camerakit.OLYCamera;
import jp.sfjp.gokigen.a01c.olycamerawrapper.ICameraHardwareStatus;

/**
 *   カメラプロパティをやり取りするクラス (Wrapperクラス)
 */
public class OlyCameraPropertyProxy implements IOlyCameraPropertyProvider
{
    private final String TAG = toString();
    private final OLYCamera camera;
    private final OlyCameraHardwareStatus hardwareStatusInterface;

    /**
     *   コンストラクタ
     *
     * @param camera OLYCameraクラス
     */
    public OlyCameraPropertyProxy(OLYCamera camera)
    {
        this.camera = camera;
        this.hardwareStatusInterface = new OlyCameraHardwareStatus(camera);
    }

    /**
     *  フォーカス状態を知る（MF or AF）
     * @return true : MF / false : AF
     */
    public boolean isManualFocus()
    {
        boolean isManualFocus = false;
        try
        {
            String value = camera.getCameraPropertyValue(IOlyCameraProperty.FOCUS_STILL);
            Log.v(TAG, "OlyCameraPropertyProxy::isManualFocus() " + value);
            isManualFocus = !(value.contains("AF"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (isManualFocus);
    }

    /**
     *  AE ロック状態を知る
     *
     * @return true : AE Lock / false : AE Unlock
     */
    public boolean isExposureLocked()
    {
        boolean isExposureLocked =false;
        try
        {
            String value = camera.getCameraPropertyValue(IOlyCameraProperty.AE_LOCK_STATE);
            Log.v(TAG, "OlyCameraPropertyProxy::isExposureLocked() " + value);
            isExposureLocked = !(value.contains("UNLOCK"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (isExposureLocked);
    }

    @Override
    public Set<String> getCameraPropertyNames()
    {
        try
        {
            return (camera.getCameraPropertyNames());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public String getCameraPropertyValue(String name)
    {
        try
        {
            return (camera.getCameraPropertyValue(name));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public Map<String, String> getCameraPropertyValues(Set<String> names)
    {
        try
        {
            return (camera.getCameraPropertyValues(names));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public String getCameraPropertyTitle(String name)
    {
        try
        {
            return (camera.getCameraPropertyTitle(name));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public List<String> getCameraPropertyValueList(String name)
    {
        try
        {
            return (camera.getCameraPropertyValueList(name));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public String getCameraPropertyValueTitle(String propertyValue)
    {
        try
        {
            return (camera.getCameraPropertyValueTitle(propertyValue));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (null);
    }

    @Override
    public void setCameraPropertyValue(String name, String value)
    {
        try
        {
            camera.setCameraPropertyValue(name, value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void setCameraPropertyValues(Map<String, String> values)
    {
        try
        {
            camera.setCameraPropertyValues(values);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *  カメラプロパティの選択肢を１段階あげる
     */
    @Override
    public void updateCameraPropertyUp(String name)
    {
        String current = getCameraPropertyValue(name);
        List<String> valueList = getCameraPropertyValueList(name);
        int index = valueList.indexOf(current);
        if (index < (valueList.size() - 1))
        {
            try
            {
                index++;
                String targetValue = valueList.get(index);
                Log.v(TAG, "updateCameraPropertyUp() : " + name + " , " + targetValue);
                setCameraPropertyValue(name, targetValue);
            }
            catch (Exception e)
            {
                //
                e.printStackTrace();
            }
        }
    }

    /**
     *  カメラプロパティの選択肢を１段階下げる
     */
    @Override
    public void updateCameraPropertyDown(String name)
    {
        String current = getCameraPropertyValue(name);
        List<String> valueList = getCameraPropertyValueList(name);
        int index = valueList.indexOf(current);
        if (index > 0)
        {
            try
            {
                index--;
                String targetValue = valueList.get(index);
                Log.v(TAG, "updateCameraPropertyDown() : " + name + " , " + targetValue);
                setCameraPropertyValue(name, targetValue);
            }
            catch (Exception e)
            {
                //
                e.printStackTrace();
            }
        }
    }

    /**
     *   カメラプロパティの選択肢を direction段階分更新する
     *
     */
    @Override
    public void changeCameraProperty(String name, int direction)
    {
        String current = getCameraPropertyValue(name);
        List<String> valueList = getCameraPropertyValueList(name);
        int index = valueList.indexOf(current) + direction;
        if (index < 0)
        {
            // 下限を下回ったら、上限にする
            index = valueList.size() - 1;
        }
        else if (index > (valueList.size() - 1))
        {
            // 上限を上回ったら、下限にする
            index = 0;
        }
        try
        {
            String targetValue = valueList.get(index);
            Log.v(TAG, "changeCameraProperty() : " + name + " , " + targetValue);
            setCameraPropertyValue(name, targetValue);
        }
        catch (Exception e)
        {
            //
            e.printStackTrace();
        }

    }

    @Override
    public boolean canSetCameraProperty(String name)
    {
        try
        {
            return (camera.canSetCameraProperty(name));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }

    @Override
    public boolean isConnected()
    {
        try
        {
            return (camera.isConnected());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (false);
    }

    @Override
    public ICameraHardwareStatus getHardwareStatus()
    {
        return (hardwareStatusInterface);
    }

}
