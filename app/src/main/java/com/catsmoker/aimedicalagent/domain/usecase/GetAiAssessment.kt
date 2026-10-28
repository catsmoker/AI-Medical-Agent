package com.catsmoker.aimedicalagent.domain.usecase

import com.catsmoker.aimedicalagent.domain.model.AiAssessment
import com.catsmoker.aimedicalagent.domain.model.UserProfile
import com.catsmoker.aimedicalagent.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAiAssessment @Inject constructor(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(
        message: String,
        imageBytes: ByteArray?,
        imageMimeType: String?,
        language: String,
        profile: UserProfile
    ): AiAssessment = aiRepository.assess(message, imageBytes, imageMimeType, language, profile)

    fun stream(
        message: String,
        imageBytes: ByteArray?,
        imageMimeType: String?,
        language: String,
        profile: UserProfile
    ): Flow<AiAssessment> = aiRepository.assessStream(message, imageBytes, imageMimeType, language, profile)

    open fun isVague(message: String): Boolean = aiRepository.isVague(message)
}
