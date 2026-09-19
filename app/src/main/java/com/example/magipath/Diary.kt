package com.example.magipath

import android.content.Context

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
    private const val SEP = "|"
    private const val LINE = "\n"

    fun getXp(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_XP, 0)
    }

    fun addXp(context: Context, amount: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_XP, 0)
        prefs.edit().putInt(KEY_XP, current + amount).apply()
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
