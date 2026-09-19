package com.example.magipath

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    App()
                }
            }
        }
    }
}

// ---------- Хранилище прогресса ----------

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

// ---------- Модель упражнений ----------

data class ExerciseStep(val title: String, val description: String)

data class Exercise(
    val id: String,
    val title: String,
    val subtitle: String,
    val steps: List<ExerciseStep>
)

val exercises = listOf(
    Exercise(
        id = "anchor",
        title = "Якорь",
        subtitle = "Точка и простые фигуры",
        steps = listOf(
            ExerciseStep(
                "Точка",
                "Закрой глаза. Поставь в центре внутреннего пространства светлую точку. Если не видишь — просто знай, что она есть. Держи 30 секунд."
            ),
            ExerciseStep(
                "Линия",
                "Растяни точку в тонкую линию. Подержи горизонтально, потом вертикально."
            ),
            ExerciseStep(
                "Круг",
                "Согни линию в круг. Медленно вращай его по часовой стрелке."
            ),
            ExerciseStep(
                "Квадрат",
                "Преврати круг в квадрат. Пройдись по углам, заметь цвет."
            ),
            ExerciseStep(
                "Куб",
                "Вытяни квадрат в куб. Вращай его, различай грани."
            )
        )
    ),
    Exercise(
        id = "relax",
        title = "Расслабление",
        subtitle = "Три этапа телесного покоя",
        steps = listOf(
            ExerciseStep(
                "Этап 1. Тело",
                "Ляг или сядь удобно. Закрой глаза. Поочерёдно расслабляй кисти, руки, ноги, спину, лицо. Наполняй их тёплой светящейся тяжестью. Цель: уложиться в 3–5 минут."
            ),
            ExerciseStep(
                "Этап 2. Ритм",
                "Ощути тепло в животе и холод во лбу. Поймай ритм дыхания и сердца, не управляя им. Дай им замедлиться. Цель: уложиться в 2–3 минуты."
            ),
            ExerciseStep(
                "Этап 3. Отчуждение",
                "Представь, что от ног к голове проходит плоскость. Выше неё — тёплое тяжёлое тело, ниже — невесомое, чужое. Медленно доведи плоскость до макушки."
            )
        )
    )
)

// ---------- Приложение ----------

@Composable
fun App() {
    var screen by remember { mutableStateOf("menu") }
    var currentId by remember { mutableStateOf<String?>(null) }

    when (screen) {
        "menu" -> MenuScreen(
            onOpen = { id ->
                currentId = id
                screen = "exercise"
            }
        )
        "exercise" -> {
            val ex = exercises.first { it.id == currentId }
            ExerciseScreen(
                exercise = ex,
                onBack = { screen = "menu" }
            )
        }
    }
}

// ---------- Главное меню ----------

@Composable
fun MenuScreen(onOpen: (String) -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("МагиПуть", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Упражнения начального уровня",
            style = MaterialTheme.typography.bodyMedium
        )
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

// ---------- Экран упражнения ----------

@Composable
fun ExerciseScreen(exercise: Exercise, onBack: () -> Unit) {
    val context = LocalContext.current
    var stepIndex by remember { mutableStateOf(Progress.getStep(context, exercise.id)) }
    var seconds by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        while (running) {
            delay(1000)
            seconds++
        }
    }

    val step = exercise.steps[stepIndex.coerceIn(0, exercise.steps.size - 1)]

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
        Text("Время: $seconds сек", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { running = !running }) {
                Text(if (running) "Пауза" else "Старт")
            }
            OutlinedButton(onClick = {
                seconds = 0
                running = false
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
                    }
                },
                enabled = stepIndex > 0
            ) {
                Text("Назад")
            }
            Button(
                onClick = {
                    if (stepIndex < exercise.steps.size - 1) {
                        stepIndex++
                        Progress.setStep(context, exercise.id, stepIndex)
                        seconds = 0
                        running = false
                    } else {
                        onBack()
                    }
                }
            ) {
                Text(if (stepIndex < exercise.steps.size - 1) "Дальше" else "Готово")
            }
        }
    }
}
