package com.example.magipath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
