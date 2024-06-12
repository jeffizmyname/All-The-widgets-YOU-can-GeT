package com.example.spotwidget


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotwidget.ui.theme.SpotWidgetTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            SpotWidgetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val sharedPreferences = getSharedPreferences("WledPrefs", Context.MODE_PRIVATE)
                    val savedIpAddress = sharedPreferences.getString("WLED_IP", "") ?: ""

                    var text by rememberSaveable { mutableStateOf(savedIpAddress) }
                    Row {
                        HandleIpSteal(text) {
                            newText -> text = newText
                        }
                        Button(modifier = Modifier
                            .requiredHeight(65.dp)
                            .requiredWidth(200.dp)
                            .padding(5.dp), onClick = {
                            sharedPreferences.edit().putString("WLED_IP", text).apply()
                            },
                            ) {
                            Text("Save", fontSize = 30.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HandleIpSteal(text: String, onTextChange: (String) -> Unit) {
    OutlinedTextField(modifier = Modifier
        .requiredHeight(65.dp)
        .requiredWidth(200.dp), value = text, onValueChange = { onTextChange(it) }, label = { Text("gib wled ip")})
}

