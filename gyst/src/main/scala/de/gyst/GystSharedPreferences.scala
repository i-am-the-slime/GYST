package de.gyst

import android.content.Context
import android.preference.PreferenceManager


object GystSharedPreferences {
  val ACCOUNT = "account"

  def setAccountName(an:String)(implicit ctx:Context) = putPreference(ACCOUNT, an)
  def getAccountName()(implicit ctx:Context):Option[String] = {
    val name = getPreference(ACCOUNT, "")
    if (name != "") Some(name) else None
  }

  def putPreference[A](key:String, value:A)(implicit ctx:Context) = {
    val prefs = PreferenceManager.getDefaultSharedPreferences(ctx).edit()
    value match {
      case v: String  => prefs.putString(key, v)
      case v: Int     => prefs.putInt(key, v)
      case v: Boolean => prefs.putBoolean(key, v)
      case v: Float   => prefs.putFloat(key, v)
      case v: Long    => prefs.putLong(key, v)
      case v: java.util.Set[String] => prefs.putStringSet(key, v)
    }
    prefs.commit()
  }

  def getPreference[A](key:String, default:A)(implicit ctx:Context):A = {
    val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
    val result = default match {
      case default: String  => prefs.getString(key, default)
      case default: Int     => prefs.getInt(key, default)
      case default: Boolean => prefs.getBoolean(key, default)
      case default: Float   => prefs.getFloat(key, default)
      case default: Long    => prefs.getLong(key, default)
      case default: java.util.Set[String] => prefs.getStringSet(key, default)
    }
    result.asInstanceOf[A]
  }
}

