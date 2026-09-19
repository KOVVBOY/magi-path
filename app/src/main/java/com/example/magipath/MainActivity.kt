package com.example.magipath

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

@Composable
fun App() {
    var screen by remember { mutableStateOf("menu") }
    when (screen) {
        "menu" -> MenuScreen(onOpenAnchor = { screen = "anchor" })
        "anchor" -> AnchorScreen(onBack = { screen = "menu" })
    }
}

@Composable
fun MenuScreen(onOpenAnchor: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("МагиПуть", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        Text("Упражнение «Якорь» — точка и фигуры")
        Spacer(Modifier.height(8.dp))
        Button(onClick = onOpenAnchor) {
            Text("Открыть «Якорь»")
        }
    }
}

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

@Composable
fun AnchorScreen(onBack: () -> Unit) {
    var stepIndex by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        while (running) {
            delay(1000)
            seconds++
        }
    }

    val step = anchorSteps[stepIndex]

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
                onClick = { if (stepIndex > 0) stepIndex-- },
                enabled = stepIndex > 0
            ) {
                Text("Назад")
            }
            Button(
                onClick = {
                    if (stepIndex < anchorSteps.size - 1) {
                        stepIndex++
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
