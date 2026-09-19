package com.example.magipath

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PlanProgress {
    private const val PREFS = "magipath_prefs"
    private const val KEY_START = "plan_start"
    private const val KEY_TODAY = "plan_today"
    private const val KEY_DONE = "plan_done_today"

    private fun todayStr(): String =
        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

    fun ensureStarted(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getLong(KEY_START, 0L) == 0L) {
            prefs.edit().putLong(KEY_START, System.currentTimeMillis()).apply()
        }
    }

    fun startOver(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putLong(KEY_START, System.currentTimeMillis())
            .remove(KEY_TODAY)
            .remove(KEY_DONE)
            .apply()
    }

    fun getDaysPassed(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val start = prefs.getLong(KEY_START, 0L)
        if (start == 0L) return 0
        return ((System.currentTimeMillis() - start) / (24L * 60 * 60 * 1000)).toInt()
    }

    fun getCurrentStage(context: Context): Int {
        val days = getDaysPassed(context)
        return (days / 14).coerceIn(0, planStages.size - 1)
    }

    fun getWeekInStage(context: Context): Int {
        val days = getDaysPassed(context)
        return (days % 14) / 7 + 1
    }

    fun getTodayDone(context: Context): Set<Int> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val savedDate = prefs.getString(KEY_TODAY, "") ?: ""
        if (savedDate != todayStr()) return emptySet()
        val raw = prefs.getString(KEY_DONE, "") ?: ""
        if (raw.isEmpty()) return emptySet()
        return raw.split(",").mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun toggle(context: Context, index: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val today = todayStr()
        val savedDate = prefs.getString(KEY_TODAY, "") ?: ""
        val current = if (savedDate == today) getTodayDone(context) else emptySet()
        val updated = if (index in current) current - index else current + index
        prefs.edit()
            .putString(KEY_TODAY, today)
            .putString(KEY_DONE, updated.joinToString(","))
            .apply()
    }
}
