package com.catsmoker.aimedicalagent.ui.doctor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catsmoker.aimedicalagent.R
import com.catsmoker.aimedicalagent.domain.model.MedicalCase
import com.catsmoker.aimedicalagent.domain.repository.MedicalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DoctorQueueViewModel @Inject constructor(
    private val medicalRepository: MedicalRepository
) : ViewModel() {
    val cases: StateFlow<List<MedicalCase>> = medicalRepository.cases().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}

@Composable
fun DoctorQueueScreen(
    viewModel: DoctorQueueViewModel,
    onCaseClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val cases by viewModel.cases.collectAsState(initial = emptyList())
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
                text = stringResource(id = R.string.doctor_queue),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        if (cases.isEmpty()) {
            Text(text = stringResource(id = R.string.no_cases), style = MaterialTheme.typography.bodyMedium)
        } else {
            cases.forEach { caseItem ->
                CaseRow(
                    caseItem = caseItem,
                    onClick = { onCaseClick(caseItem.id) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun CaseRow(caseItem: MedicalCase, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = caseItem.patientMessage, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            Text(
                text = caseItem.status.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}

@Composable
fun DoctorCaseScreen(
    caseId: String,
    viewModel: DoctorCaseViewModel,
    onBack: () -> Unit
) {
    val caseItem by viewModel.caseFlow.collectAsState(initial = null)
    val currentCase = caseItem ?: return
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
                text = stringResource(id = R.string.case_detail),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = currentCase.patientMessage, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.size(12.dp))
        currentCase.aiAssessment?.let { ai ->
            Text(
                text = stringResource(id = R.string.ai_summary),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(text = ai.explanation, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { viewModel.approve() }) { Text(stringResource(id = R.string.approve)) }
            Button(onClick = { viewModel.correct() }) { Text(stringResource(id = R.string.correct)) }
            Button(onClick = { viewModel.askMore() }) { Text(stringResource(id = R.string.ask_more_info)) }
        }
    }
}

@HiltViewModel
class DoctorCaseViewModel @Inject constructor(
    private val medicalRepository: MedicalRepository
) : ViewModel() {
    val caseFlow = medicalRepository.cases().map { list -> list.firstOrNull { it.id == "pending" } }
    fun approve() {}
    fun correct() {}
    fun askMore() {}
}
