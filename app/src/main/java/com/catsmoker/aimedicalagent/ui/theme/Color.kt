package com.catsmoker.aimedicalagent.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkPrimary = Color(0xFF64B5F6)
private val DarkContainer = Color(0xFF37474F)
private val LightPrimary = Color(0xFF1976D2)
private val LightContainer = Color(0xFFE3F2FD)

private val EmergencyRed = Color(0xFFD32F2F)
private val CautionAmber = Color(0xFFF9A825)
private val CalmGreen = Color(0xFF388E3C)

@Composable
fun dynamicColorScheme(
    darkTheme: Boolean = isSystemInDarkTheme()
): ColorScheme {
    return when {
        darkTheme -> darkColorScheme(
            primary = DarkPrimary,
            primaryContainer = DarkContainer
        )
        else -> lightColorScheme(
            primary = LightPrimary,
            primaryContainer = LightContainer
        )
    }
}

val EmergencyColor = EmergencyRed
val CautionColor = CautionAmber
val SuccessColor = CalmGreen
