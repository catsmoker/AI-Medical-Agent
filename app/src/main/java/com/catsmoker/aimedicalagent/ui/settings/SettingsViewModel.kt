package com.catsmoker.aimedicalagent.ui.settings

import androidx.lifecycle.ViewModel
import com.catsmoker.aimedicalagent.domain.repository.LocaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val localeRepository: LocaleRepository
) : ViewModel() {
    val currentLanguage: String
        get() = localeRepository.currentLanguage.value
    val isSubscribed: Boolean
        get() = false
}

@HiltViewModel
class PrivacyViewModel @Inject constructor() : ViewModel() {
    val policyText: String =
        "AI Medical Agent — Privacy Policy\n\n" +
        "Your health data is sensitive. This app stores your chat history locally in an encrypted " +
        "database and sends your symptom description (and optional photo) to an AI service to " +
        "generate health information. We do not sell your data.\n\n"
}
