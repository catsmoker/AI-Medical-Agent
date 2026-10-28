package com.catsmoker.aimedicalagent.domain.repository

import com.catsmoker.aimedicalagent.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface HealthDataRepository {
    suspend fun saveProfile(profile: UserProfile)
    val profile: Flow<UserProfile?>
}
