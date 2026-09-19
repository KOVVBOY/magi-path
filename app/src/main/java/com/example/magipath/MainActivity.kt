package com.example.magipath

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
    private const val KEY_ANCHOR_STEP = "anchor_step"

    fun getAnchorStep(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_ANCHOR_STEP, 0)
    }

    fun setAnchorStep(context: Context, step: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_ANCHOR_STEP, step).apply()
    }
}

// ---------- Приложение ----------

@Composable
fun App() {
    var screen by remember { mutableStateOf("menu") }
    when (screen) {
        "menu" -> MenuScreen(onOpenAnchor = { screen = "anchor" })
        "anchor" -> AnchorScreen(onBack = { screen = "menu" })
    }
}

// ---------- Главное меню ----------

@Composable
fun MenuScreen(onOpenAnchor: () -> Unit) {
    val context = LocalContext.current
    var savedStep by remember { mutableStateOf(Progress.getAnchorStep(context)) }

    // Обновляем при возврате в меню
    LaunchedEffect(Unit) {
        savedStep = Progress.getAnchorStep(context)
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("МагиПуть", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        Text("Упражнение «Якорь» — точка и фигуры")
        Spacer(Modifier.height(8.dp))
        Text(
            "Прогресс: шаг ${savedStep + 1} из $ANCHOR_STEPS_COUNT",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))
        Button(onClick = onOpenAnchor) {
            Text("Продолжить")
        }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = {
            Progress.setAnchorStep(context, 0)
            savedStep = 0
        }) {
            Text("Начать заново")
        }
    }
}

// ---------- Упражнение «Якорь» ----------

data class AnchorStep(val title: String, val description: String)

val anchorSteps = listOf(
    AnchorStep(
        "Точка",
        "Закрой глаза. Поставь в центре внутреннего пространства светлую точку. Если не видишь — просто знай, что она есть. Держи 30 секунд."
    ),
    AnchorStep(
        "Линия",
        "Растяни точку в тонкую линию. Подержи горизонтально, потом вертикально."
    ),
    AnchorStep(
        "Круг",
        "Согни линию в круг. Медленно вращай его по часовой стрелке."
    ),
    AnchorStep(
        "Квадрат",
        "Преврати круг в квадрат. Пройдись по углам, заметь цвет."
    ),
    AnchorStep(
        "Куб",
        "Вытяни квадрат в куб. Вращай его, различай грани."
    )
)

val ANCHOR_STEPS_COUNT = anchorSteps.size

@Composable
fun AnchorScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var stepIndex by remember { mutableStateOf(Progress.getAnchorStep(context)) }
    var seconds by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        while (running) {
            delay(1000)
            seconds++
        }
    }

    val step = anchorSteps[stepIndex.coerceIn(0, anchorSteps.size - 1)]

    Column(modifier = Modifier.padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Назад") }

        Spacer(Modifier.height(16.dp))
        Text(
            "Якорь: шаг ${stepIndex + 1} из ${anchorSteps.size}",
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
                        Progress.setAnchorStep(context, stepIndex)
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
                    if (stepIndex < anchorSteps.size - 1) {
                        stepIndex++
                        Progress.setAnchorStep(context, stepIndex)
                        seconds = 0
                        running = false
                    } else {
                        onBack()
                    }
                }
            ) {
                Text(if (stepIndex < anchorSteps.size - 1) "Дальше" else "Готово")
            }
        }
    }
}
