package com.example.magipath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    var step by remember { mutableStateOf(0) }

                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("МагиПуть: шаг $step")
                        Button(
                            onClick = { step++ },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Следующий шаг")
                        }
                    }
                }
            }
        }
    }
}
