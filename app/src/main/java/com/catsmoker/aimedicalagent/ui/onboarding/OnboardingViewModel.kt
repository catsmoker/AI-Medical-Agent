package com.catsmoker.aimedicalagent.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.catsmoker.aimedicalagent.domain.repository.AuthRepository
import com.catsmoker.aimedicalagent.domain.repository.LocaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository,
    private val localeRepository: LocaleRepository
) : AndroidViewModel(application) {

    val language: String get() = localeRepository.currentLanguage.value
    val supportedLanguages: List<String> = localeRepository.supportedLanguages
}
