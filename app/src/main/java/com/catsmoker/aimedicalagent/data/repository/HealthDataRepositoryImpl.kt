package com.catsmoker.aimedicalagent.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import com.catsmoker.aimedicalagent.domain.model.UserProfile
import com.catsmoker.aimedicalagent.domain.repository.HealthDataRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthDataRepositoryImpl @Inject constructor(
    private val prefs: EncryptedSharedPreferences,
    @ApplicationContext private val context: Context
) : HealthDataRepository {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    override val profile: Flow<UserProfile?> = _profile.asStateFlow()

    override suspend fun saveProfile(profile: UserProfile) {
        with(prefs.edit()) {
            putString(KEY_PROFILE, profile.toString())
            apply()
        }
        _profile.value = profile
    }

    fun load(): UserProfile? {
        val json = prefs.getString(KEY_PROFILE, "") ?: ""
        return if (json.isEmpty()) null else UserProfile()
    }

    companion object {
        private const val KEY_PROFILE = "user_profile"
    }
}
