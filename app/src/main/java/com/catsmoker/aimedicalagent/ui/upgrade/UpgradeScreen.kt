package com.catsmoker.aimedicalagent.ui.upgrade

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun UpgradeScreen(
    onSubscribe: () -> Unit,
    onRestore: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(id = com.catsmoker.aimedicalagent.R.string.upgrade),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        Text(
            text = stringResource(id = com.catsmoker.aimedicalagent.R.string.upgrade_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(24.dp))

        PlanCard(
            name = stringResource(id = com.catsmoker.aimedicalagent.R.string.plan_pay_per_review),
            description = stringResource(id = com.catsmoker.aimedicalagent.R.string.plan_pay_per_review_desc),
            price = stringResource(id = com.catsmoker.aimedicalagent.R.string.plan_pay_per_review_price),
            onClick = onSubscribe
        )
        Spacer(modifier = Modifier.size(12.dp))
        PlanCard(
            name = stringResource(id = com.catsmoker.aimedicalagent.R.string.plan_monthly),
            description = stringResource(id = com.catsmoker.aimedicalagent.R.string.plan_monthly_desc),
            price = stringResource(id = com.catsmoker.aimedicalagent.R.string.plan_monthly_price),
            onClick = onSubscribe
        )
        Spacer(modifier = Modifier.size(16.dp))
        TextButton(onClick = onRestore) {
            Text(stringResource(id = com.catsmoker.aimedicalagent.R.string.restore_purchases))
        }
    }
}

@Composable
private fun PlanCard(name: String, description: String, price: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = price, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}
