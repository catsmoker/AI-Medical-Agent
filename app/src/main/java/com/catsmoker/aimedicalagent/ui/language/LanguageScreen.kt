package com.catsmoker.aimedicalagent.ui.language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.catsmoker.aimedicalagent.domain.repository.LocaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val localeRepository: LocaleRepository
) : ViewModel() {
    val languages: List<String> = localeRepository.supportedLanguages
    val currentLanguage: String
        get() = localeRepository.currentLanguage.value
    fun select(lang: String) {
        localeRepository.setLanguage(lang)
    }
}

@Composable
fun LanguageScreen(
    viewModel: LanguageViewModel,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Select Language", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            viewModel.languages.forEach { lang ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.select(lang) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(lang, style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Done")
            }
        }
    }
}
