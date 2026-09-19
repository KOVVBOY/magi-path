package com.example.magipath

import android.content.Context

object Settings {
    private const val PREFS = "magipath_prefs"
    private const val KEY_DARK = "dark_theme"
    private const val KEY_SOUND = "sound_on"

    fun isDark(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK, false)
    }

    fun setDark(context: Context, value: Boolean) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK, value).apply()
    }

    fun isSoundOn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SOUND, true)
    }

    fun setSoundOn(context: Context, value: Boolean) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SOUND, value).apply()
    }

    fun resetAll(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
