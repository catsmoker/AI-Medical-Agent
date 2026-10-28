package com.catsmoker.aimedicalagent.data.remote

import com.catsmoker.aimedicalagent.config.AiConfig
import com.catsmoker.aimedicalagent.domain.model.AiAssessment
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the Gemini generateContent endpoint.
 *
 * The real API key must NOT be compiled into the APK. It is supplied via
 * local.properties -> BuildConfig.GEMINI_API_KEY (development only, TODO:
 * migrate to Firebase AI Logic + App Check or a backend proxy before production).
 */
interface GeminiService {

    data class GenerationConfig(
        val temperature: Double? = null,
        val topP: Double? = null,
        val topK: Int? = null,
        val maxOutputTokens: Int? = null
    )

    data class Part(val text: String?, val inlineData: InlineData?)
    data class InlineData(val mimeType: String, val data: String) // base64
    data class Content(val role: String, val parts: List<Part>)
    data class GenerateContentRequest(
        val contents: List<Content>,
        val generationConfig: GenerationConfig? = null,
        val safetySettings: List<SafetySetting>? = null
    )
    data class SafetySetting(
        val category: String = "HARM_CATEGORY_DANGEROUS_CONTENT",
        val threshold: String = "BLOCK_MEDIUM_AND_ABOVE"
    )

    data class GenerateContentResponse(
        val candidates: List<Candidate>? = null
    ) {
        data class Candidate(
            val content: Content? = null,
            val finishReason: String? = null
        )
    }

    @POST("models/{model}:generateContent")
    fun generateContent(
        @Query("key") apiKey: String,
        @Path("model") model: String,
        @Body request: GenerateContentRequest
    ): Call<GenerateContentResponse>
}
