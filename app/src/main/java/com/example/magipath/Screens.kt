package com.example.magipath

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MenuScreen(
    onOpen: (String) -> Unit,
    onOpenDiary: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val xp = Diary.getXp(context)
    val level = Diary.getLevel(context)
    val xpInLevel = Diary.getXpInLevel(context)
    val streak = Diary.getStreak(context)
    val entryCount = Diary.getEntries(context).size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("МагиПуть", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        Text(
            "Уровень $level",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { xpInLevel / 100f },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Опыт: $xpInLevel / 100 до уровня ${level + 1}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(8.dp))
        Text(
            "Дней подряд: $streak",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Всего опыта: $xp XP · Записей: $entryCount",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onOpenDiary) {
                Text("Дневник")
            }
            OutlinedButton(onClick = onOpenSettings) {
                Text("Настройки")
            }
        }

        Spacer(Modifier.height(20.dp))

        exercises.forEach { ex ->
            val saved = Progress.getStep(context, ex.id)
            val done = saved >= ex.steps.size - 1
            val status = if (done) {
                "Завершено"
            } else {
                "Прогресс: шаг ${saved + 1} из ${ex.steps.size}"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(ex.id) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(ex.title, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    Text(ex.subtitle, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(status, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

val TIME_MARKERS = listOf(60, 180, 300, 600)

@Composable
fun ExerciseScreen(exercise: Exercise, onBack: () -> Unit) {
    val context = LocalContext.current
    var stepIndex by remember { mutableStateOf(Progress.getStep(context, exercise.id)) }
    var seconds by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isLastStep by remember { mutableStateOf(false) }
    var soundOn by remember { mutableStateOf(Settings.isSoundOn(context)) }
    var firedMarkers by remember { mutableStateOf(setOf<Int>()) }

    val tone = remember {
        ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
    }
    DisposableEffect(Unit) {
        onDispose { tone.release() }
    }

    LaunchedEffect(running) {
        while (running) {
            delay(1000)
            seconds++
        }
    }

    LaunchedEffect(seconds) {
        if (seconds in TIME_MARKERS && seconds !in firedMarkers) {
            firedMarkers = firedMarkers + seconds
            if (soundOn) {
                tone.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
            }
        }
    }

    val step = exercise.steps[stepIndex.coerceIn(0, exercise.steps.size - 1)]

    if (showDialog) {
        FeelDialog(
            onPick = { feel, note ->
                Diary.addEntry(
                    context,
                    DiaryEntry(
                        exerciseId = exercise.id,
                        stepIndex = stepIndex,
                        feel = feel,
                        note = note,
                        timestamp = System.currentTimeMillis()
                    )
                )
                Diary.addXp(context, 10)
                Diary.registerPracticeToday(context)
                showDialog = false
                if (isLastStep) {
                    onBack()
                } else {
                    stepIndex++
                    Progress.setStep(context, exercise.id, stepIndex)
                    seconds = 0
                    running = false
                    firedMarkers = emptySet()
                }
            },
            onCancel = { showDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Назад") }

        Spacer(Modifier.height(16.dp))
        Text(
            "${exercise.title}: шаг ${stepIndex + 1} из ${exercise.steps.size}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))
        Text(step.title, style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))
        Text(step.description)

        Spacer(Modifier.height(24.dp))
        Text(
            "Время: ${formatTime(seconds)}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TIME_MARKERS.forEach { marker ->
                val reached = seconds >= marker
                Text(
                    text = "${marker / 60}м",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (reached) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = soundOn,
                onCheckedChange = {
                    soundOn = it
                    Settings.setSoundOn(context, it)
                }
            )
            Text("Звук на 1, 3, 5, 10 минутах")
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { running = !running }) {
                Text(if (running) "Пауза" else "Старт")
            }
            OutlinedButton(onClick = {
                seconds = 0
                running = false
                firedMarkers = emptySet()
            }) {
                Text("Сброс")
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    if (stepIndex > 0) {
                        stepIndex--
                        Progress.setStep(context, exercise.id, stepIndex)
                        seconds = 0
                        running = false
                        firedMarkers = emptySet()
                    }
                },
                enabled = stepIndex > 0
            ) {
                Text("Назад")
            }
            Button(
                onClick = {
                    isLastStep = stepIndex >= exercise.steps.size - 1
                    showDialog = true
                }
            ) {
                Text(if (stepIndex < exercise.steps.size - 1) "Дальше" else "Готово")
            }
        }
    }
}

fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}

@Composable
fun FeelDialog(onPick: (String, String) -> Unit, onCancel: () -> Unit) {
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Как прошёл шаг?") },
        text = {
            Column {
                Text("Отметь состояние. Заметка — по желанию.")
                Spacer(Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onPick("easy", note) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Легко")
                    }
                    OutlinedButton(
                        onClick = { onPick("normal", note) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Обычно")
                    }
                    OutlinedButton(
                        onClick = { onPick("hard", note) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Тяжело")
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Заметка") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onCancel) { Text("Отмена") }
        }
    )
}

@Composable
fun DiaryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val entries = Diary.getEntries(context).reversed()
    val xp = Diary.getXp(context)
    val level = Diary.getLevel(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Назад") }

        Spacer(Modifier.height(16.dp))
        Text("Дневник", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Уровень $level · $xp XP",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(20.dp))

        if (entries.isEmpty()) {
            Text("Пока пусто. Пройди любой шаг — запись появится здесь.")
        } else {
            val fmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

            entries.forEach { entry ->
                val ex = exercises.firstOrNull { it.id == entry.exerciseId }
                val exTitle = ex?.title ?: entry.exerciseId
                val stepTitle = ex?.steps?.getOrNull(entry.stepIndex)?.title
                    ?: "шаг ${entry.stepIndex + 1}"
                val feelText = when (entry.feel) {
                    "easy" -> "Легко"
                    "hard" -> "Тяжело"
                    else -> "Обычно"
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "$exTitle — $stepTitle",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("Состояние: $feelText")
                        if (entry.note.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Заметка: ${entry.note}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            fmt.format(Date(entry.timestamp)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun SettingsScreen(
    dark: Boolean,
    onDarkChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Сбросить всё?") },
            text = {
                Text(
                    "Весь прогресс, опыт, дневник и настройки будут удалены. " +
                        "Это действие нельзя отменить."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    Settings.resetAll(context)
                    showResetDialog = false
                    onBack()
                }) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Назад") }

        Spacer(Modifier.height(16.dp))
        Text("Настройки", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = dark,
                onCheckedChange = onDarkChange
            )
            Text("Тёмная тема")
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сбросить весь прогресс")
        }
    }
}
