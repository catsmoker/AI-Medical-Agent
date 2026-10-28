package com.catsmoker.aimedicalagent.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catsmoker.aimedicalagent.R
import com.catsmoker.aimedicalagent.domain.model.Role

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onRoleSelected: (Role) -> Unit
) {
    var selectedRole by remember { mutableStateOf<Role?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(24.dp))
        Text(
            text = stringResource(id = R.string.welcome),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(id = R.string.choose_role),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RoleCard(
                label = stringResource(id = R.string.role_patient),
                icon = Icons.Default.Person,
                selected = selectedRole == Role.PATIENT,
                onClick = {
                    selectedRole = Role.PATIENT
                    onRoleSelected(Role.PATIENT)
                }
            )
            RoleCard(
                label = stringResource(id = R.string.role_doctor),
                icon = Icons.Default.LocalHospital,
                selected = selectedRole == Role.DOCTOR,
                onClick = {
                    selectedRole = Role.DOCTOR
                    onRoleSelected(Role.DOCTOR)
                }
            )
        }

        Spacer(modifier = Modifier.size(32.dp))

        Text(
            text = stringResource(id = R.string.consent_notice),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun RoleCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(110.dp)
            .border(
                if (selected) 2.dp else 0.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(16.dp)
            )
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
