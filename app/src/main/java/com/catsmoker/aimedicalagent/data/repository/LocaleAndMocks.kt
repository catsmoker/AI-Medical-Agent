package com.catsmoker.aimedicalagent.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import com.catsmoker.aimedicalagent.domain.model.Role
import com.catsmoker.aimedicalagent.domain.repository.AuthRepository
import com.catsmoker.aimedicalagent.domain.repository.LocaleRepository
import com.catsmoker.aimedicalagent.domain.repository.PaymentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : AuthRepository {

    private val _userId = MutableStateFlow<String?>(null)
    override val currentUserId: Flow<String?> = _userId.asStateFlow()

    private val _role = MutableStateFlow<Role?>(null)
    override val currentUserRole: Flow<Role?> = _role.asStateFlow()

    override val isAuthenticated: Flow<Boolean> = _userId.map { it != null }
    override val isDoctorVerified: Flow<Boolean> = _role.map { it == Role.DOCTOR }

    override suspend fun signInAnonymously() {
        _userId.value = "patient_mock_123"
        _role.value = Role.PATIENT
    }

    override suspend fun signInDoctor(email: String, password: String) {
        _userId.value = "doctor_mock_$email"
        _role.value = Role.DOCTOR
    }

    override suspend fun signOut() {
        _userId.value = null
        _role.value = null
    }

    override suspend fun submitDoctorVerification(licenseNumber: String, documentUri: String) {
        _role.value = Role.DOCTOR
    }
}

@Singleton
class LocaleRepositoryImpl @Inject constructor(
    private val prefs: EncryptedSharedPreferences,
    @ApplicationContext private val context: Context
) : LocaleRepository {

    private val _language = MutableStateFlow(prefs.getString(KEY_LANG, "en") ?: "en")
    override val currentLanguage: StateFlow<String> = _language.asStateFlow()

    override val supportedLanguages: List<String> = listOf("en", "fr", "ar")

    override fun setLanguage(language: String) {
        require(language in supportedLanguages)
        with(prefs.edit()) {
            putString(KEY_LANG, language)
            apply()
        }
        _language.value = language
        applyAppLocale(language)
    }

    private fun applyAppLocale(language: String) {
        val res = context.resources
        val configuration = res.configuration
        configuration.setLocale(Locale(language))
        res.updateConfiguration(configuration, res.displayMetrics)
    }

    fun requireAppLocale(context: Context): String {
        val lang = prefs.getString(KEY_LANG, "en") ?: "en"
        applyAppLocale(lang)
        return lang
    }

    companion object {
        private const val KEY_LANG = "selected_language"
    }
}

@Singleton
class PaymentRepositoryMock @Inject constructor() : PaymentRepository {
    private val _subscription = MutableStateFlow(
        com.catsmoker.aimedicalagent.domain.model.Subscription(
            tier = com.catsmoker.aimedicalagent.domain.model.SubscriptionTier.NONE,
            active = false
        )
    )

    override val subscription: Flow<com.catsmoker.aimedicalagent.domain.model.Subscription>
        get() = _subscription.asStateFlow()

    override suspend fun purchaseDoctorReview(caseId: String) {}

    override suspend fun subscribeMonthly() {
        _subscription.value = _subscription.value.copy(
            tier = com.catsmoker.aimedicalagent.domain.model.SubscriptionTier.MONTHLY,
            active = true
        )
    }

    override suspend fun restorePurchases() {}
}
