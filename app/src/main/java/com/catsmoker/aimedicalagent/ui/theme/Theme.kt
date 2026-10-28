package com.catsmoker.aimedicalagent.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme(
            primary = Color(0xFF64B5F6),
            secondary = Color(0xFF4DD0E1),
            background = Color(0xFF0B1215),
            surface = Color(0xFF16212A),
            error = Color(0xFFD32F2F)
        )
        else -> lightColorScheme(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFF0288D1),
            background = Color(0xFFF5F9FC),
            surface = Color.White,
            error = Color(0xFFD32F2F)
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AimTypography,
        content = content
    )
}

object SafetyColors {
    val EmergencyBackground = Color(0xFF1A0A0A)
    val EmergencyBorder = Color(0xFFD32F2F)
    val EmergencyText = Color.White
}
