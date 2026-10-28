package com.catsmoker.aimedicalagent.domain.repository

import com.catsmoker.aimedicalagent.domain.model.AiAssessment
import com.catsmoker.aimedicalagent.domain.model.MedicalCase
import com.catsmoker.aimedicalagent.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Source of truth for health cases, history and AI assessments.
 * Implementations may be backed by Firebase Firestore + local Room cache.
 */
interface MedicalRepository {
    /** Stream of cases for the current user (patient or doctor). */
    fun cases(): Flow<List<MedicalCase>>

    fun caseById(caseId: String): Flow<MedicalCase?>

    /** Create a new patient case with an AI assessment. Returns the new case id. */
    suspend fun createCase(
        patientId: String,
        message: String,
        photoUri: String?,
        language: String,
        aiAssessment: AiAssessment,
        profile: UserProfile
    ): String

    /** Doctor actions. */
    suspend fun approveCase(caseId: String, doctorId: String)
    suspend fun correctCase(caseId: String, doctorId: String, corrected: AiAssessment, notes: String)
    suspend fun requestMoreInfo(caseId: String, doctorId: String, question: String)
    suspend fun sendProductLink(caseId: String, doctorId: String, productName: String)

    /** Add a chat message inside a case (doctor <-> patient). */
    suspend fun addCaseMessage(caseId: String, senderId: String, senderRole: String, text: String)

    /** Local history (Room) of past assessments for the patient. */
    fun localHistory(): Flow<List<MedicalCase>>

    suspend fun saveLocalHistory(case: MedicalCase)
    suspend fun deleteAllLocalData()
}
