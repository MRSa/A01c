package jp.sfjp.gokigen.a01c.preference

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.core.content.edit

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
            preferences.edit {
                if (!items.containsKey(IPreferenceCameraPropertyAccessor.CONNECTION_METHOD)) {
                    putString(
                        IPreferenceCameraPropertyAccessor.CONNECTION_METHOD,
                        IPreferenceCameraPropertyAccessor.CONNECTION_METHOD_DEFAULT_VALUE
                    )
                }
                if (!items.containsKey(IPreferenceCameraPropertyAccessor.THETA_GL_VIEW)) {
                    putBoolean(IPreferenceCameraPropertyAccessor.THETA_GL_VIEW, false)
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }
}
