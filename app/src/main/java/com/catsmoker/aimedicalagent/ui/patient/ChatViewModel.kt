package com.catsmoker.aimedicalagent.ui.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catsmoker.aimedicalagent.domain.model.AiAssessment
import com.catsmoker.aimedicalagent.domain.model.MedicalCase
import com.catsmoker.aimedicalagent.domain.model.UrgencyLevel
import com.catsmoker.aimedicalagent.domain.model.UserProfile
import com.catsmoker.aimedicalagent.domain.repository.LocaleRepository
import com.catsmoker.aimedicalagent.domain.repository.MedicalRepository
import com.catsmoker.aimedicalagent.domain.repository.ProductLinkProvider
import com.catsmoker.aimedicalagent.domain.usecase.GetAiAssessment
import com.catsmoker.aimedicalagent.util.EmergencyDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getAiAssessment: GetAiAssessment,
    private val medicalRepository: MedicalRepository,
    private val localeRepository: LocaleRepository,
    private val productLinkProvider: ProductLinkProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        localeRepository.currentLanguage.onEach { lang ->
            _uiState.update { it.copy(language = lang) }
        }.launchIn(viewModelScope)
    }

    fun setMessage(text: String) {
        _uiState.update { it.copy(message = text) }
    }

    fun setLanguage(lang: String) {
        localeRepository.setLanguage(lang)
        _uiState.update { it.copy(language = lang) }
    }

    fun sendMessage(imageBytes: ByteArray?, imageMimeType: String?) {
        val msg = _uiState.value.message.trim()
        if (msg.isEmpty() && imageBytes == null) return

        val lang = _uiState.value.language
        val profile = _uiState.value.profile
        val clientEmergency = EmergencyDetector.isEmergency(msg, lang)

        _uiState.update { it.copy(
            isLoading = true,
            error = null,
            latestAssessment = null,
            showingEmergency = false
        )}

        viewModelScope.launch {
            val assessment = if (getAiAssessment.isVague(msg)) {
                AiAssessment(
                    explanation = when (lang) {
                        "fr" -> "Pourriez-vous préciser vos symptômes ? (durée, intensité, localisation)"
                        "ar" -> "هل يمكنك توضيح أعراضك؟ (المدة، الشدة، المكان)"
                        else -> "Could you describe your symptoms in more detail? (duration, intensity, location)"
                    },
                    urgencyLevel = UrgencyLevel.SELF_CARE,
                    followUpQuestions = listOf(
                        "How long have you had these symptoms?",
                        "Where exactly do you feel it?",
                        "On a scale of 1-10, how severe is it?"
                    )
                )
            } else {
                getAiAssessment(msg, imageBytes, imageMimeType, lang, profile)
            }

            val serverEmergency = assessment.urgencyLevel == UrgencyLevel.EMERGENCY
            val isEmergency = clientEmergency || serverEmergency

            _uiState.update { it.copy(
                isLoading = false,
                latestAssessment = assessment,
                showingEmergency = isEmergency
            )}

            if (!isEmergency) {
                val profileForSave = profile
                viewModelScope.launch {
                    val id = medicalRepository.createCase(
                        patientId = "current_patient",
                        message = msg,
                        photoUri = null,
                        language = lang,
                        aiAssessment = assessment,
                        profile = profileForSave
                    )
                    _uiState.update { it.copy(lastCaseId = id) }
                }
            }
        }
    }

    fun buildProductLink(productName: String): String {
        return productLinkProvider.searchUrl(productName, productLinkProvider.defaultSites.firstOrNull() ?: "amazon.com")
    }

    fun onEmergencyDismissed() {
        _uiState.update { it.copy(showingEmergency = false) }
    }

    data class UiState(
        val message: String = "",
        val language: String = "en",
        val isLoading: Boolean = false,
        val error: String? = null,
        val latestAssessment: AiAssessment? = null,
        val showingEmergency: Boolean = false,
        val profile: UserProfile = UserProfile(),
        val isSubscribed: Boolean = false,
        val history: List<MedicalCase> = emptyList(),
        val lastCaseId: String? = null,
        val selectedCase: MedicalCase? = null
    )
}
