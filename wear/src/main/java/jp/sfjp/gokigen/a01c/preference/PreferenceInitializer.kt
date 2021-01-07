package jp.sfjp.gokigen.a01c.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferenceInitializer
{
    fun initializePreferences(context : Context)
    {
        try
        {
            initializeApplicationPreferences(context)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun initializeApplicationPreferences(context : Context)
    {
        try
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context) ?: return
            val items : Map<String, *> = preferences.all
            val editor : SharedPreferences.Editor = preferences.edit()
            if (!items.containsKey(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD))
            {
                editor.putString(
                        IPreferenceCameraPropertyAccessor.CONNECTION_METHOD,
                        IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE
                )
            }
            editor.apply()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }
}
