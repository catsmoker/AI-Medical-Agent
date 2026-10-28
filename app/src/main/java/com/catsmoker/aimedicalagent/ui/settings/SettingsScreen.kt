package com.catsmoker.aimedicalagent.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.catsmoker.aimedicalagent.ui.theme.EmergencyColor

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onLanguageClick: () -> Unit,
    onUpgradeClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onDeleteData: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(id = com.catsmoker.aimedicalagent.R.string.settings),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.size(16.dp))

        SettingsRow(
            icon = Icons.Default.Language,
            title = stringResource(id = com.catsmoker.aimedicalagent.R.string.language),
            subtitle = viewModel.currentLanguage.uppercase(),
            onClick = onLanguageClick
        )
        HorizontalDivider()
        SettingsRow(
            icon = Icons.Default.Star,
            title = stringResource(id = com.catsmoker.aimedicalagent.R.string.subscription),
            subtitle = if (viewModel.isSubscribed) stringResource(id = com.catsmoker.aimedicalagent.R.string.active) else stringResource(id = com.catsmoker.aimedicalagent.R.string.inactive),
            onClick = onUpgradeClick
        )
        HorizontalDivider()
        SettingsRow(
            icon = Icons.Default.Security,
            title = stringResource(id = com.catsmoker.aimedicalagent.R.string.privacy_policy),
            subtitle = null,
            onClick = onPrivacyClick
        )
        HorizontalDivider()
        SettingsRow(
            icon = Icons.Default.Delete,
            title = stringResource(id = com.catsmoker.aimedicalagent.R.string.delete_my_data),
            subtitle = null,
            onClick = onDeleteData,
            titleColor = EmergencyColor
        )
        HorizontalDivider()
        SettingsRow(
            icon = Icons.Default.Person,
            title = stringResource(id = com.catsmoker.aimedicalagent.R.string.sign_out),
            subtitle = null,
            onClick = onSignOut
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    titleColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.size(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = titleColor)
            subtitle?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
