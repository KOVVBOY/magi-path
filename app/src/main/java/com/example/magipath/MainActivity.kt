package com.example.magipath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    val context = LocalContext.current
    var dark by remember { mutableStateOf(Settings.isDark(context)) }
    var screen by remember { mutableStateOf("menu") }
    var currentId by remember { mutableStateOf<String?>(null) }

    val scheme = if (dark) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = scheme) {
        Surface {
            when (screen) {
                "menu" -> MenuScreen(
                    onOpen = { id ->
                        currentId = id
                        screen = "exercise"
                    },
                    onOpenDiary = { screen = "diary" },
                    onOpenSettings = { screen = "settings" }
                )
                "exercise" -> {
                    val ex = exercises.first { it.id == currentId }
                    ExerciseScreen(
                        exercise = ex,
                        onBack = { screen = "menu" }
                    )
                }
                "diary" -> DiaryScreen(onBack = { screen = "menu" })
                "settings" -> SettingsScreen(
                    dark = dark,
                    onDarkChange = {
                        dark = it
                        Settings.setDark(context, it)
                    },
                    onBack = {
                        // после сброса вернёмся в меню
                        screen = "menu"
                    }
                )
            }
        }
    }
}
