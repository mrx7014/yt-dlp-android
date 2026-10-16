package com.mrx7014.ytdlp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Blue = Color(0xFF3559E0)
private val Light = lightColorScheme(primary = Blue, primaryContainer = Color(0xFFDDE2FF))
private val Dark = darkColorScheme(primary = Color(0xFFB9C3FF), primaryContainer = Color(0xFF1E2B65))

@Composable
fun YtDlpTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
