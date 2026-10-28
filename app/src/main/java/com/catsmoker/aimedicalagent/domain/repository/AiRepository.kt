package com.catsmoker.aimedicalagent.domain.repository

import com.catsmoker.aimedicalagent.domain.model.AiAssessment
import com.catsmoker.aimedicalagent.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Talks to the Gemini AI. Returns a structured [AiAssessment].
 * Supports text-only and multimodal (text + image) input with streaming.
 */
interface AiRepository {
    /** Non-streaming assessment. */
    suspend fun assess(
        message: String,
        imageBytes: ByteArray?,
        imageMimeType: String?,
        language: String,
        profile: UserProfile
    ): AiAssessment

    /** Streaming assessment; emits [AiAssessment] chunks as they arrive. */
    fun assessStream(
        message: String,
        imageBytes: ByteArray?,
        imageMimeType: String?,
        language: String,
        profile: UserProfile
    ): Flow<AiAssessment>

    val isVague: (String) -> Boolean
}
