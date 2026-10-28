package com.catsmoker.aimedicalagent.data.repository

import com.catsmoker.aimedicalagent.data.local.HistoryEntity
import com.catsmoker.aimedicalagent.data.local.HistoryLocalDataSource
import com.catsmoker.aimedicalagent.domain.model.AiAssessment
import com.catsmoker.aimedicalagent.domain.model.CaseStatus
import com.catsmoker.aimedicalagent.domain.model.MedicalCase
import com.catsmoker.aimedicalagent.domain.model.UserProfile
import com.catsmoker.aimedicalagent.domain.repository.MedicalRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicalRepositoryImpl @Inject constructor(
    private val localDataSource: HistoryLocalDataSource,
    private val gson: Gson
) : MedicalRepository {

    private val _cases = MutableStateFlow(emptyList<MedicalCase>())
    override fun cases(): Flow<List<MedicalCase>> = _cases.asStateFlow()

    override fun caseById(caseId: String): Flow<MedicalCase?> =
        cases().map { list -> list.firstOrNull { it.id == caseId } }

    override suspend fun createCase(
        patientId: String,
        message: String,
        photoUri: String?,
        language: String,
        aiAssessment: AiAssessment,
        profile: UserProfile
    ): String {
        val id = java.util.UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val entity = HistoryEntity(
            id = id,
            patientId = patientId,
            patientMessage = message,
            photoUri = photoUri,
            language = language,
            status = "WAITING_FOR_DOCTOR",
            createdAt = now,
            updatedAt = now,
            aiAssessmentJson = gson.toJson(aiAssessment)
        )
        localDataSource.save(entity)

        val caseObj = MedicalCase(
            id = id,
            patientId = patientId,
            patientMessage = message,
            photoUri = photoUri,
            language = language,
            aiAssessment = aiAssessment,
            status = CaseStatus.WAITING_FOR_DOCTOR,
            createdAt = java.util.Date(now),
            updatedAt = java.util.Date(now)
        )
        _cases.update { listOf(caseObj) + it }
        return id
    }

    override suspend fun approveCase(caseId: String, doctorId: String) {
        updateStatus(caseId, CaseStatus.APPROVED, doctorId)
    }

    override suspend fun correctCase(caseId: String, doctorId: String, corrected: AiAssessment, notes: String) {
        val index = _cases.value.indexOfFirst { it.id == caseId }
        if (index >= 0) {
            val current = _cases.value[index]
            val updated = current.copy(
                status = CaseStatus.CORRECTED,
                doctorId = doctorId,
                doctorNotes = notes,
                correctedAssessment = corrected,
                updatedAt = java.util.Date()
            )
            _cases.update { list ->
                list.mapIndexed { i, c -> if (i == index) updated else c }
            }
        }
    }

    override suspend fun requestMoreInfo(caseId: String, doctorId: String, question: String) {
        updateStatus(caseId, CaseStatus.DOCTOR_NEEDS_INFO, doctorId)
    }

    override suspend fun sendProductLink(caseId: String, doctorId: String, productName: String) {
        val index = _cases.value.indexOfFirst { it.id == caseId }
        if (index >= 0) {
            val current = _cases.value[index]
            val note = "Product link suggested: $productName. ${current.doctorNotes ?: ""}"
            _cases.update { list ->
                list.mapIndexed { i, c ->
                    if (i == index) c.copy(doctorNotes = note, updatedAt = java.util.Date()) else c
                }
            }
        }
    }

    override suspend fun addCaseMessage(caseId: String, senderId: String, senderRole: String, text: String) {
        val index = _cases.value.indexOfFirst { it.id == caseId }
        if (index >= 0) {
            val current = _cases.value[index]
            val note = "${current.doctorNotes ?: ""}\n[$senderRole] $senderId: $text"
            _cases.update { list ->
                list.mapIndexed { i, c ->
                    if (i == index) c.copy(doctorNotes = note.trim(), updatedAt = java.util.Date()) else c
                }
            }
        }
    }

    override fun localHistory(): Flow<List<MedicalCase>> = localDataSource.all().map { entities ->
        entities.map { e ->
            val assessment = e.aiAssessmentJson?.let { gson.fromJson(it, AiAssessment::class.java) }
            val statusVal = try { CaseStatus.valueOf(e.status) } catch (ex: Exception) { CaseStatus.WAITING_FOR_DOCTOR }
            MedicalCase(
                id = e.id,
                patientId = e.patientId,
                patientMessage = e.patientMessage,
                photoUri = e.photoUri,
                language = e.language,
                aiAssessment = assessment,
                status = statusVal,
                createdAt = java.util.Date(e.createdAt),
                updatedAt = java.util.Date(e.updatedAt)
            )
        }
    }

    override suspend fun saveLocalHistory(case: MedicalCase) {
        val entity = HistoryEntity(
            id = case.id,
            patientId = case.patientId,
            patientMessage = case.patientMessage,
            photoUri = case.photoUri,
            language = case.language,
            status = case.status.name,
            createdAt = case.createdAt.time,
            updatedAt = case.updatedAt.time,
            aiAssessmentJson = gson.toJson(case.aiAssessment)
        )
        localDataSource.save(entity)
    }

    override suspend fun deleteAllLocalData() {
        localDataSource.deleteAll()
        _cases.update { emptyList() }
    }

    private fun updateStatus(caseId: String, status: CaseStatus, doctorId: String?) {
        val index = _cases.value.indexOfFirst { it.id == caseId }
        if (index >= 0) {
            val current = _cases.value[index]
            _cases.update { list ->
                list.mapIndexed { i, c ->
                    if (i == index) c.copy(status = status, doctorId = doctorId, updatedAt = java.util.Date()) else c
                }
            }
        }
    }
}
