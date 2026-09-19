package com.example.magipath

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DiaryEntry(
    val exerciseId: String,
    val stepIndex: Int,
    val feel: String,
    val timestamp: Long
)

object Diary {
    private const val PREFS = "magipath_prefs"
    private const val KEY_ENTRIES = "diary_entries"
    private const val KEY_XP = "xp"
    private const val KEY_STREAK = "streak"
    private const val KEY_LAST_DATE = "last_practice_date"
    private const val SEP = "|"
    private const val LINE = "\n"

    private fun fmt(): SimpleDateFormat =
        SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    private fun today(): String = fmt().format(Date())

    private fun yesterday(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return fmt().format(cal.time)
    }

    fun getXp(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_XP, 0)
    }

    fun addXp(context: Context, amount: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_XP, 0)
        prefs.edit().putInt(KEY_XP, current + amount).apply()
    }

    fun getLevel(context: Context): Int = getXp(context) / 100 + 1

    fun getXpInLevel(context: Context): Int = getXp(context) % 100

    fun getStreak(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_STREAK, 0)
    }

    fun registerPracticeToday(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val last = prefs.getString(KEY_LAST_DATE, "") ?: ""
        val t = today()
        if (last == t) return
        val prevStreak = prefs.getInt(KEY_STREAK, 0)
        val newStreak = if (last == yesterday()) prevStreak + 1 else 1
        prefs.edit()
            .putInt(KEY_STREAK, newStreak)
            .putString(KEY_LAST_DATE, t)
            .apply()
    }

    fun addEntry(context: Context, entry: DiaryEntry) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getString(KEY_ENTRIES, "") ?: ""
        val line = listOf(
            entry.exerciseId,
            entry.stepIndex.toString(),
            entry.feel,
            entry.timestamp.toString()
        ).joinToString(SEP)
        val updated = if (current.isEmpty()) line else current + LINE + line
        prefs.edit().putString(KEY_ENTRIES, updated).apply()
    }

    fun getEntries(context: Context): List<DiaryEntry> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_ENTRIES, "") ?: ""
        if (raw.isEmpty()) return emptyList()
        return raw.split(LINE).mapNotNull { line ->
            val parts = line.split(SEP)
            if (parts.size < 4) return@mapNotNull null
            DiaryEntry(
                exerciseId = parts[0],
                stepIndex = parts[1].toIntOrNull() ?: 0,
                feel = parts[2],
                timestamp = parts[3].toLongOrNull() ?: 0L
            )
        }
    }
}
