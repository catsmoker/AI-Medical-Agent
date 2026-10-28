package com.catsmoker.aimedicalagent.ui.healthprofile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catsmoker.aimedicalagent.R
import com.catsmoker.aimedicalagent.domain.model.UserProfile
import com.catsmoker.aimedicalagent.domain.repository.HealthDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthProfileViewModel @Inject constructor(
    private val repository: HealthDataRepository
) : ViewModel() {
    var age by mutableStateOf("")
    var sex by mutableStateOf("")
    var allergies by mutableStateOf("")
    var pregnant by mutableStateOf(false)
    var chronicConditions by mutableStateOf("")
    var medications by mutableStateOf("")

    fun save() {
        val profile = UserProfile(
            age = age.toIntOrNull(),
            sex = sex.takeIf { it.isNotEmpty() },
            allergies = allergies.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            isPregnant = pregnant,
            chronicConditions = chronicConditions.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            currentMedications = medications.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        )
        viewModelScope.launch { repository.saveProfile(profile) }
    }
}

@Composable
fun HealthProfileScreen(viewModel: HealthProfileViewModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(id = R.string.health_profile),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(id = R.string.health_profile_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(16.dp))

        OutlinedTextField(
            value = viewModel.age,
            onValueChange = { viewModel.age = it },
            label = { Text(stringResource(id = R.string.age)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(12.dp))
        OutlinedTextField(
            value = viewModel.sex,
            onValueChange = { viewModel.sex = it },
            label = { Text(stringResource(id = R.string.sex)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(12.dp))
        OutlinedTextField(
            value = viewModel.allergies,
            onValueChange = { viewModel.allergies = it },
            label = { Text(stringResource(id = R.string.allergies)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = viewModel.pregnant,
                onCheckedChange = { viewModel.pregnant = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.pregnant))
        }
        Spacer(modifier = Modifier.size(12.dp))
        OutlinedTextField(
            value = viewModel.chronicConditions,
            onValueChange = { viewModel.chronicConditions = it },
            label = { Text(stringResource(id = R.string.chronic_conditions)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(12.dp))
        OutlinedTextField(
            value = viewModel.medications,
            onValueChange = { viewModel.medications = it },
            label = { Text(stringResource(id = R.string.current_medications)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(24.dp))
        Button(onClick = { viewModel.save() }) {
            Text(stringResource(id = R.string.save))
        }
    }
}
