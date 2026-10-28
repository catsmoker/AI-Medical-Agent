package com.catsmoker.aimedicalagent.ui.patient

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.catsmoker.aimedicalagent.R
import com.catsmoker.aimedicalagent.domain.model.AiAssessment
import com.catsmoker.aimedicalagent.domain.model.UrgencyLevel
import com.catsmoker.aimedicalagent.ui.theme.AimTheme
import com.catsmoker.aimedicalagent.ui.theme.CautionColor
import com.catsmoker.aimedicalagent.ui.theme.EmergencyColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AimTheme {
                val viewModel: ChatViewModel = hiltViewModel()
                PatientChatScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PatientChatScreen(viewModel: ChatViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.sendMessage(null, null) }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DisclaimerBanner()

        if (uiState.showingEmergency) {
            EmergencyWarningBanner(onDismiss = { viewModel.onEmergencyDismissed() })
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (uiState.message.isNotEmpty()) {
                item { MessageBubble(text = uiState.message, isUser = true) }
            }
            uiState.latestAssessment?.let { assessment ->
                item {
                    AiResponseCard(
                        assessment = assessment,
                        language = uiState.language,
                        onProductClick = { productName ->
                            val url = viewModel.buildProductLink(productName)
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        },
                        onEmergency = {}
                    )
                }
            }
            if (uiState.isLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            uiState.error?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            var imageBytes by remember { mutableStateOf<ByteArray?>(null) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { pickImage.launch("image/*") }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Attach photo")
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice input")
                }
                OutlinedTextField(
                    value = uiState.message,
                    onValueChange = { viewModel.setMessage(it) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.hint_describe_symptoms),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = false,
                    maxLines = 4
                )
                IconButton(
                    onClick = {
                        viewModel.sendMessage(imageBytes, "image/jpeg")
                    },
                    enabled = uiState.message.isNotEmpty() || imageBytes != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = { }) { Text("History") }
            OutlinedButton(onClick = { }) { Text("Upgrade") }
            OutlinedButton(onClick = { }) { Text("Settings") }
        }
    }
}

@Composable
private fun DisclaimerBanner() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Text(
            text = "This is not a medical diagnosis. For informational purposes only — consult a licensed professional.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun EmergencyWarningBanner(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(EmergencyColor, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Emergency warning",
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text(
                    text = "Possible emergency detected.",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Call emergency services immediately. AI product suggestions are disabled.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = Color.White)
            }
        }
    }
}

@Composable
fun MessageBubble(text: String, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .background(
                    if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                    RoundedCornerShape(16.dp)
                )
                .padding(12.dp, 8.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AiResponseCard(
    assessment: AiAssessment,
    language: String,
    onProductClick: (String) -> Unit,
    onEmergency: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.ai_assessment),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        when (assessment.urgencyLevel) {
            UrgencyLevel.EMERGENCY -> {
                Surface(
                    color = EmergencyColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("EMERGENCY", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            UrgencyLevel.SEE_DOCTOR -> {
                Surface(
                    color = CautionColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("SEE DOCTOR", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            else -> { }
        }

        Text(
            text = assessment.explanation,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )

        if (assessment.possibleConditions.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.possible_conditions),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
            assessment.possibleConditions.forEach { c ->
                Text(
                    text = "${c.condition} (${c.likelihood})",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                )
            }
        }

        if (assessment.selfCareSteps.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.self_care),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
            assessment.selfCareSteps.forEach { step ->
                Text(
                    text = "• $step",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                )
            }
        }

        if (assessment.otcProductNames.isNotEmpty() && assessment.urgencyLevel != UrgencyLevel.EMERGENCY) {
            Text(
                text = stringResource(id = R.string.otc_suggestions),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
            assessment.otcProductNames.forEach { product ->
                TextButton(onClick = { onProductClick(product) }) {
                    Text(product, modifier = Modifier.padding(start = 16.dp))
                }
            }
            Text(
                text = stringResource(id = R.string.otc_disclaimer),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }

        if (assessment.redFlags.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.red_flags),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = EmergencyColor
                ),
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
            assessment.redFlags.forEach { flag ->
                Text(
                    text = "⚠ $flag",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmergencyColor,
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                )
            }
        }

        if (assessment.followUpQuestions.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.follow_up_questions),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
            assessment.followUpQuestions.forEach { q ->
                Text(
                    text = "? $q",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                )
            }
        }

        Text(
            text = stringResource(id = R.string.disclaimer_short),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(8.dp)
        )
    }
}
