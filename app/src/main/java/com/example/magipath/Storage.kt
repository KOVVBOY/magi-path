package com.example.magipath

import android.content.Context

object Progress {
    private const val PREFS = "magipath_prefs"

    fun getStep(context: Context, exerciseId: String): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt("step_$exerciseId", 0)
    }

    fun setStep(context: Context, exerciseId: String, step: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putInt("step_$exerciseId", step).apply()
    }
}
