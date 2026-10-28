package com.catsmoker.aimedicalagent.domain.repository

import com.catsmoker.aimedicalagent.domain.model.Role
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: Flow<String?>
    val currentUserRole: Flow<Role?>
    val isAuthenticated: Flow<Boolean>
    val isDoctorVerified: Flow<Boolean>

    suspend fun signInAnonymously()
    suspend fun signInDoctor(email: String, password: String)
    suspend fun signOut()

    /** Doctor verification: license number + document upload (admin-approved, mocked). */
    suspend fun submitDoctorVerification(licenseNumber: String, documentUri: String)
}
