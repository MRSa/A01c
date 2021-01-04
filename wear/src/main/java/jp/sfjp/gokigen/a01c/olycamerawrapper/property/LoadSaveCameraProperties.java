package jp.sfjp.gokigen.a01c.olycamerawrapper.property;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import jp.co.olympus.camerakit.OLYCamera;
import jp.sfjp.gokigen.a01c.olycamerawrapper.IOLYCameraObjectProvider;

/**
 *   カメラプロパティを一括でバックアップしたり、リストアしたりするクラス
 *
 */
public class LoadSaveCameraProperties implements ILoadSaveCameraProperties
{
    private final String TAG = toString();

    private final Context parent;
    private final OLYCamera camera;
    private final IOlyCameraPropertyProvider propertyProvider;

    public LoadSaveCameraProperties(Context context, IOlyCameraPropertyProvider propertyProvider, IOLYCameraObjectProvider provider)
    {
        this.camera = provider.getOLYCamera();
        this.parent = context;
        this.propertyProvider = propertyProvider;
    }

    /**
     *   カメラの現在の設定を本体から読みだして記憶する
     *
     */
    @Override
    public void saveCameraSettings(String idHeader, String dataName)
    {
        // カメラから設定を一括で読みだして、Preferenceに記録する
        if (propertyProvider.isConnected())
        {
            Map<String, String> values = null;
            try
            {
                values = propertyProvider.getCameraPropertyValues(camera.getCameraPropertyNames());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            //Log.v(TAG, "CameraPropertyBackupRestore::storeCameraSettings() : " + idHeader);

            if (values != null)
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
                SharedPreferences.Editor editor = preferences.edit();
                for (String key : values.keySet())
                {
                    editor.putString(idHeader + key, values.get(key));
                    //Log.v(TAG, "storeCameraSettings(): " + idHeader + key + " , " + values.get(key));
                }
                DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
                editor.putString(idHeader + DATE_KEY, dateFormat.format(new Date()));
                editor.putString(idHeader + TITLE_KEY, dataName);
                //editor.commit();
                editor.apply();

                Log.v(TAG, "storeCameraSettings() COMMITED : " + idHeader + " [" + dataName + "]");
            }
        }
    }

    /**
     *   Preferenceにあるカメラの設定をカメラに登録する
     *　(注： Read Onlyなパラメータを登録しようとするとエラーになるので注意）
     */
    @Override
    public void loadCameraSettings(String idHeader)
    {
        Log.v(TAG, "loadCameraSettings() : START [" + idHeader + "]");
        //loadCameraSettingsBatch(idHeader);
        //loadCameraSettingsMiniBatch(idHeader, 5);
        //loadCameraSettingsSequential(idHeader);
        loadCameraSettingsOnlyDifferences(idHeader);
    }

/*
        ///**
        // *   カメラのプロパティを１つづつ個別設定
        // *
        // *
    private void loadCameraSettingsSequential(String idHeader)
    {
        int setCount = 0;
        // Restores my settings.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        if (camera.isConnected())
        {
            String takeModeValue = preferences.getString(idHeader + TAKEMODE, null);
            try
            {
                // TAKEMODE だけは先行して設定する（設定できないカメラプロパティもあるので...）
                if (takeModeValue != null)
                {
                    camera.setCameraPropertyValue(TAKEMODE, takeModeValue);
                    Log.v(TAG, "loadCameraSettings() TAKEMODE : " + takeModeValue);
                    setCount++;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.v(TAG, "loadCameraSettings() : loadCameraSettingsSequential() fail...");
            }

            Set<String> names = camera.getCameraPropertyNames();
            for (String name : names)
            {
                String value = preferences.getString(idHeader + name, null);
                if (value != null)
                {
                    if (propertyProvider.canSetCameraProperty(name))
                    {
                        // Read Onlyのプロパティを除外して登録
                        try
                        {
                            // カメラプロパティを個別登録（全パラメータを一括登録すると何か落ちている
                            Log.v(TAG, "loadCameraSettingsSequential(): " + value);
                            camera.setCameraPropertyValue(name, value);
                            setCount++;
                            //Thread.sleep(5);   //　処理落ちしている？かもしれないので必要なら止める
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Log.v(TAG, "loadCameraSettingsSequential() : END [" + idHeader + "]" + " " + setCount);
        }
    }

    //// プロパティの一括設定
    private void loadCameraSettingsBatch(String idHeader)
    {
        // Restores my settings.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        if (camera.isConnected())
        {
            String takeModeValue = preferences.getString(idHeader + TAKEMODE, null);
            try
            {
                // TAKEMODE だけは先行して設定する（設定できないカメラプロパティもあるので...）
                if (takeModeValue != null)
                {
                    camera.setCameraPropertyValue(TAKEMODE, takeModeValue);
                    Log.v(TAG, "loadCameraSettings() TAKEMODE : " + takeModeValue);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.v(TAG, "loadCameraSettings() : setCameraPropertyValue() fail...");
            }

            Map<String, String> values = new HashMap<>();
            Set<String> names = camera.getCameraPropertyNames();
            for (String name : names)
            {
                String value = preferences.getString(idHeader + name, null);
                if (value != null)
                {
                    if (propertyProvider.canSetCameraProperty(name))
                    {
                        // Read Onlyのプロパティを除外して登録
                        values.put(name, value);
                        Log.v(TAG, "loadCameraSettings(): " + value);
                    }
                }
            }
            if (values.size() > 0)
            {
                try
                {
                    camera.setCameraPropertyValues(values);
                }
                catch (OLYCameraKitException e)
                {
                    Log.w(TAG, "To change the camera properties is failed: " + e.getMessage());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            Log.v(TAG, "loadCameraSettingsBatch() : END [" + idHeader + "]" + " " + values.size());
        }
    }

    //// プロパティの一括設定 (ミニバッチ)
    private void loadCameraSettingsMiniBatch(String idHeader, int batchSize)
    {
        // Restores my settings.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        if (camera.isConnected())
        {
            String takeModeValue = preferences.getString(idHeader + TAKEMODE, null);
            try
            {
                // TAKEMODE だけは先行して設定する（設定できないカメラプロパティもあるので...）
                if (takeModeValue != null)
                {
                    camera.setCameraPropertyValue(TAKEMODE, takeModeValue);
                    Log.v(TAG, "loadCameraSettings() TAKEMODE : " + takeModeValue);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.v(TAG, "loadCameraSettings() : setCameraPropertyValue() fail...");
            }

            int currentSize = 0;
            Map<String, String> values = new HashMap<>();
            Set<String> names = camera.getCameraPropertyNames();
            for (String name : names)
            {
                String value = preferences.getString(idHeader + name, null);
                if (value != null)
                {
                    if (propertyProvider.canSetCameraProperty(name))
                    {
                        // Read Onlyのプロパティを除外して登録
                        values.put(name, value);
                        currentSize++;
                        Log.v(TAG, "loadCameraSettings(): " + value + " [" + (currentSize) + "]");
                    }
                }
                if (currentSize >= batchSize)
                {
                    // バッチサイズの最大数到達...設定する
                    try
                    {
                        camera.setCameraPropertyValues(values);
                        Log.v(TAG, "setCameraPropertyValues() : " + currentSize);
                    }
                    catch (OLYCameraKitException e)
                    {
                        Log.w(TAG, "camera.setCameraPropertyValues() failed: " + e.getMessage());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    currentSize = 0;
                    values.clear();
                }
            }
            if (values.size() > 0)
            {
                try
                {
                    camera.setCameraPropertyValues(values);
                }
                catch (OLYCameraKitException e)
                {
                    Log.w(TAG, "To change the camera properties is failed: " + e.getMessage());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            Log.v(TAG, "loadCameraSettingsBatch() : END [" + idHeader + "]" + " " + values.size());
        }
    }
*/
    /**
     *   カメラのプロパティを１つづつ個別設定（違っているものだけ設定する）
     *
     */
    private boolean loadCameraSettingsOnlyDifferences(String idHeader)
    {
        boolean ret = false;
        int setCount = 0;

        // Restores my settings.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        if (camera.isConnected())
        {

            //  現在の設定値を全部とってくる
            Map<String, String> propertyValues;
            try
            {
                propertyValues = propertyProvider.getCameraPropertyValues(propertyProvider.getCameraPropertyNames());
            }
            catch (Exception e)
            {
                // 設定値が取得できなかった場合は、終了する。
                e.printStackTrace();
                return (false);
            }
            if (propertyValues == null)
            {
                // プロパティの取得が失敗していたら、何もせずに折り返す
                return (false);
            }

            String takeModeValue = preferences.getString(idHeader + IOlyCameraProperty.TAKE_MODE, null);
            try
            {
                // TAKEMODE だけは先行して設定する（設定できないカメラプロパティもあるので...）
                if (takeModeValue != null)
                {
                    propertyProvider.setCameraPropertyValue(IOlyCameraProperty.TAKE_MODE, takeModeValue);
                    Log.v(TAG, "loadCameraSettingsOnlyDifferences() TAKEMODE : " + takeModeValue);
                    setCount++;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.v(TAG, "loadCameraSettings() : loadCameraSettingsOnlyDifferences() fail...");
            }

            Set<String> names = propertyProvider.getCameraPropertyNames();
            for (String name : names)
            {
                String value = preferences.getString(idHeader + name, null);
                String currentValue = propertyValues.get(name);
                if ((value != null)&&(currentValue != null)&&(!value.equals(currentValue)))
                //if (value != null)
                {
                    if (propertyProvider.canSetCameraProperty(name))
                    {
                        // Read Onlyのプロパティを除外して登録
                        try
                        {
                            // カメラプロパティを個別登録（全パラメータを一括登録すると何か落ちている
                            Log.v(TAG, "loadCameraSettingsOnlyDifferences(): SET : " + value);
                            propertyProvider.setCameraPropertyValue(name, value);
                            setCount++;
                            //Thread.sleep(5);   //　処理落ちしている？かもしれないので必要なら止める
                            ret = true;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            ret = false;
                        }
                    }
                }
            }
            Log.v(TAG, "loadCameraSettingsOnlyDifferences() : END [" + idHeader + "]" + " " + setCount);
        }
        return (ret);
    }

}
