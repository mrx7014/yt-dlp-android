package com.mrx7014.ytdlp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrx7014.ytdlp.ui.HomeScreen
import com.mrx7014.ytdlp.ui.MainViewModel
import com.mrx7014.ytdlp.ui.theme.YtDlpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { YtDlpTheme { Surface(color = MaterialTheme.colorScheme.background) { HomeScreen(viewModel()) } } } }
}
