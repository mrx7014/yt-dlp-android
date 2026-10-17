package com.mrx7014.ytdlp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Obsidian = Color(0xFF0C0C0E)
private val Panel = Color(0xFF17171A)
private val PanelRaised = Color(0xFF202024)
private val Red = Color(0xFFFF2533)
private val RedSoft = Color(0xFF4A171C)
private val Gold = Color(0xFFF2BF4A)
private val GoldSoft = Color(0xFF4A3818)
private val TextPrimary = Color(0xFFF7F5F1)
private val TextSecondary = Color(0xFFB7B3AE)
private val Outline = Color(0xFF35353B)

private val PremiumDark = darkColorScheme(
    primary = Red,
    onPrimary = Color.White,
    primaryContainer = RedSoft,
    onPrimaryContainer = TextPrimary,
    secondary = Gold,
    onSecondary = Color(0xFF17120A),
    secondaryContainer = GoldSoft,
    onSecondaryContainer = TextPrimary,
    background = Obsidian,
    onBackground = TextPrimary,
    surface = Panel,
    onSurface = TextPrimary,
    surfaceVariant = PanelRaised,
    onSurfaceVariant = TextSecondary,
    outline = Outline,
    error = Color(0xFFFF8A80)
)

@Composable
fun YtDlpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PremiumDark,
        typography = Typography(),
        content = content
    )
}
