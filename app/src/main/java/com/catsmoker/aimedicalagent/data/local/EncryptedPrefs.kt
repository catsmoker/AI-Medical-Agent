package com.catsmoker.aimedicalagent.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences

class EncryptedPrefs(private val prefs: EncryptedSharedPreferences) {
    fun putString(key: String, value: String?) = with(prefs.edit()) {
        value?.let { putString(key, it) } ?: remove(key)
        apply()
    }

    fun getString(key: String, default: String = ""): String =
        prefs.getString(key, default) ?: default

    fun putBoolean(key: String, value: Boolean) = with(prefs.edit()) {
        putBoolean(key, value)
        apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    fun remove(key: String) = with(prefs.edit()) {
        remove(key)
        apply()
    }

    fun clear() = with(prefs.edit()) {
        clear()
        apply()
    }

    companion object {
        fun create(context: Context, prefs: EncryptedSharedPreferences): EncryptedPrefs =
            EncryptedPrefs(prefs)
    }
}
