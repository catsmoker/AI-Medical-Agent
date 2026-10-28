package com.catsmoker.aimedicalagent.data.repository

import android.content.Context
import android.util.Base64
import android.util.Log
import com.catsmoker.aimedicalagent.config.AiConfig
import com.catsmoker.aimedicalagent.config.MedicalSystemPrompt
import com.catsmoker.aimedicalagent.data.remote.GeminiService
import com.catsmoker.aimedicalagent.domain.model.AiAssessment
import com.catsmoker.aimedicalagent.domain.model.ConditionLikelihood
import com.catsmoker.aimedicalagent.domain.model.Likelihood
import com.catsmoker.aimedicalagent.domain.model.UrgencyLevel
import com.catsmoker.aimedicalagent.domain.model.UserProfile
import com.catsmoker.aimedicalagent.domain.repository.AiRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiService: GeminiService,
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) : AiRepository {

    private val apiKey: String
        get() = loadApiKey()

    private fun loadApiKey(): String {
        return try {
            context.getString(com.catsmoker.aimedicalagent.R.string.gemini_api_key)
        } catch (e: Exception) {
            "AIzaPlaceholder_TODO_replace_me"
        }
    }

    override suspend fun assess(
        message: String,
        imageBytes: ByteArray?,
        imageMimeType: String?,
        language: String,
        profile: UserProfile
    ): AiAssessment = withContext(Dispatchers.Default) {
        val response = geminiService.generateContent(
            apiKey,
            AiConfig.MODEL,
            buildRequest(message, imageBytes, imageMimeType, language, profile)
        ).execute()
        parseResponse(response)
    }

    override fun assessStream(
        message: String,
        imageBytes: ByteArray?,
        imageMimeType: String?,
        language: String,
        profile: UserProfile
    ): Flow<AiAssessment> = flow {
        emit(assess(message, imageBytes, imageMimeType, language, profile))
    }

    override val isVague: (String) -> Boolean = { text ->
        text.trim().length < 10
    }

    private fun buildRequest(
        message: String,
        imageBytes: ByteArray?,
        imageMimeType: String?,
        language: String,
        profile: UserProfile
    ): GeminiService.GenerateContentRequest {
        val parts = mutableListOf<GeminiService.Part>()

        val contextText = buildSystemPrompt(language, profile)
        parts.add(GeminiService.Part(text = contextText, inlineData = null))

        if (imageBytes != null && imageMimeType != null) {
            val base64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            parts.add(
                GeminiService.Part(
                    text = null,
                    inlineData = GeminiService.InlineData(imageMimeType, base64)
                )
            )
        }

        parts.add(GeminiService.Part(text = "User message: $message", inlineData = null))

        return GeminiService.GenerateContentRequest(
            contents = listOf(GeminiService.Content(role = "user", parts = parts)),
            generationConfig = GeminiService.GenerationConfig(
                temperature = 0.3,
                maxOutputTokens = 1024
            ),
            safetySettings = listOf(GeminiService.SafetySetting())
        )
    }

    private fun buildRequestBody(
        message: String,
        imageBytes: ByteArray?,
        imageMimeType: String?,
        language: String,
        profile: UserProfile
    ): RequestBody {
        val request = buildRequest(message, imageBytes, imageMimeType, language, profile)
        val json = gson.toJson(request)
        return json.toRequestBody("application/json; charset=utf-8".toMediaType())
    }

    private fun buildSystemPrompt(language: String, profile: UserProfile): String {
        val profileContext = buildProfileContext(profile)
        return "${MedicalSystemPrompt.TEXT.format(language)}\n\nHealth profile context (optional, may be empty):\n$profileContext"
    }

    private fun buildProfileContext(profile: UserProfile): String {
        val lines = mutableListOf<String>()
        profile.age?.let { lines.add("Age: $it") }
        profile.sex?.let { lines.add("Sex: $it") }
        if (profile.allergies.isNotEmpty()) lines.add("Allergies: ${profile.allergies.joinToString(", ")}")
        profile.isPregnant?.let { lines.add("Pregnant: $it") }
        if (profile.chronicConditions.isNotEmpty()) lines.add("Chronic conditions: ${profile.chronicConditions.joinToString(", ")}")
        if (profile.currentMedications.isNotEmpty()) lines.add("Current medications: ${profile.currentMedications.joinToString(", ")}")
        return lines.joinToString("\n") { "  - $it" }.takeIf { it.isNotEmpty() } ?: "None provided."
    }

    private suspend fun parseResponse(response: retrofit2.Response<GeminiService.GenerateContentResponse>): AiAssessment {
        return withContext(Dispatchers.Default) {
            if (!response.isSuccessful || response.body() == null) {
                AiAssessment(rawText = "API request failed: ${response.code()}")
            } else {
                val text = response.body()?.candidates?.firstOrNull()?.content?.parts
                    ?.filter { it.text != null }
                    ?.map { it.text!! }
                    ?.joinToString("")
                    ?: ""
                parseJsonToAssessment(text)
            }
        }
    }

    private fun parseJsonToAssessment(text: String): AiAssessment {
        val clean = text.trim()
            .removeSurrounding("```json")
            .removeSurrounding("```")
            .trim()
        return try {
            val json = gson.fromJson(clean, com.google.gson.JsonObject::class.java)
            val conditions = mutableListOf<ConditionLikelihood>()
            json.getAsJsonArray("possible_conditions")?.forEach { obj ->
                val condObj = obj.asJsonObject
                conditions.add(
                    ConditionLikelihood(
                        condition = condObj.get("condition").getAsString(),
                        likelihood = when (condObj.get("likelihood").getAsString().lowercase()) {
                            "high" -> Likelihood.HIGH
                            "medium" -> Likelihood.MEDIUM
                            else -> Likelihood.LOW
                        }
                    )
                )
            }
            val urgency = when (json.get("urgency_level").getAsString().lowercase()) {
                "emergency" -> UrgencyLevel.EMERGENCY
                "see_doctor" -> UrgencyLevel.SEE_DOCTOR
                else -> UrgencyLevel.SELF_CARE
            }
            AiAssessment(
                possibleConditions = conditions,
                explanation = json.get("explanation").getAsString(),
                selfCareSteps = json.getAsJsonArray("self_care_steps")?.map { it.getAsString() }?.toList() ?: emptyList(),
                otcProductNames = json.getAsJsonArray("otc_product_names")?.map { it.getAsString() }?.toList() ?: emptyList(),
                redFlags = json.getAsJsonArray("red_flags")?.map { it.getAsString() }?.toList() ?: emptyList(),
                urgencyLevel = urgency,
                followUpQuestions = json.getAsJsonArray("follow_up_questions")?.map { it.getAsString() }?.toList() ?: emptyList(),
                rawText = clean
            )
        } catch (e: Exception) {
            Log.e("AiRepositoryImpl", "Failed to parse AI JSON: $e\nRaw: $text")
            AiAssessment(
                explanation = clean,
                urgencyLevel = UrgencyLevel.SELF_CARE,
                rawText = clean
            )
        }
    }
}
