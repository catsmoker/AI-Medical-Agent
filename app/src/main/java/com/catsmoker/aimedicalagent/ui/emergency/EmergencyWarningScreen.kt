package com.catsmoker.aimedicalagent.ui.emergency

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.catsmoker.aimedicalagent.R
import com.catsmoker.aimedicalagent.ui.theme.EmergencyColor
import com.catsmoker.aimedicalagent.ui.theme.SafetyColors

@Composable
fun EmergencyWarningScreen(
    onDismiss: () -> Unit,
    onCallEmergency: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SafetyColors.EmergencyBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Emergency",
            tint = EmergencyColor,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.size(24.dp))
        Text(
            text = stringResource(id = R.string.emergency_detected),
            style = MaterialTheme.typography.displaySmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(id = R.string.emergency_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f)),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.size(32.dp))
        Button(onClick = onCallEmergency, modifier = Modifier.width(200.dp)) {
            Text(stringResource(id = R.string.call_emergency), color = Color.White)
        }
        Spacer(modifier = Modifier.size(12.dp))
        TextButton(onClick = onDismiss) {
            Text(stringResource(id = R.string.dismiss), color = EmergencyColor)
        }
    }
}
