package jp.sfjp.gokigen.a01c.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceManager

class PreferenceAccessWrapper(private val context : Context) : PreferenceDataStore()
{
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun initialize()
    {
        try
        {
            PreferenceInitializer().initializePreferences(context)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getString(key : String, defaultValue : String?) : String
    {
        try
        {
            val value = preferences.getString(key, defaultValue)
            if (value != null)
            {
                return (value)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (defaultValue ?: "")
    }

    override fun getBoolean(key : String, defaultValue : Boolean) : Boolean
    {
        try
        {
            return (preferences.getBoolean(key, defaultValue))
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (defaultValue)
    }

    override fun getInt(key : String, defaultValue : Int) : Int
    {
        try
        {
            return (preferences.getInt(key, defaultValue))
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (defaultValue)
    }

    override fun putString(key: String, value: String?)
    {
        try
        {
            val editor : SharedPreferences.Editor = preferences.edit()
            editor.putString(key, value)
            editor.apply()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun putBoolean(key: String, value: Boolean)
    {
        try
        {
            val editor : SharedPreferences.Editor = preferences.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

}
