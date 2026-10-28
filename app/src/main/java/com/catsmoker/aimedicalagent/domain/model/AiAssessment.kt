package com.catsmoker.aimedicalagent.domain.model

import java.util.Date

/** Result of a single AI health assessment. Structured JSON from Gemini. */
data class AiAssessment(
    val possibleConditions: List<ConditionLikelihood> = emptyList(),
    val explanation: String = "",
    val selfCareSteps: List<String> = emptyList(),
    val otcProductNames: List<String> = emptyList(), // generic names only
    val redFlags: List<String> = emptyList(),
    val urgencyLevel: UrgencyLevel = UrgencyLevel.SELF_CARE,
    val followUpQuestions: List<String> = emptyList(),
    val rawText: String = ""
)

data class ConditionLikelihood(
    val condition: String,
    val likelihood: Likelihood // low / medium / high
)

enum class Likelihood { LOW, MEDIUM, HIGH }

enum class UrgencyLevel {
    SELF_CARE,      // manage at home
    SEE_DOCTOR,     // book a review
    EMERGENCY       // call emergency services
}

enum class CaseStatus {
    WAITING_FOR_DOCTOR,
    DOCTOR_NEEDS_INFO,
    APPROVED,
    CORRECTED
}

/** A case in the system — patient request + AI summary + optional doctor review. */
data class MedicalCase(
    val id: String = "",
    val patientId: String = "",
    val doctorId: String? = null,
    val patientMessage: String = "",
    val photoUri: String? = null, // stored reference (Firebase Storage path or local)
    val language: String = "en",
    val aiAssessment: AiAssessment? = null,
    val status: CaseStatus = CaseStatus.WAITING_FOR_DOCTOR,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val doctorNotes: String? = null,
    val correctedAssessment: AiAssessment? = null
)

/** Optional health profile the patient can supply (fed to AI as context). */
data class UserProfile(
    val age: Int? = null,
    val sex: String? = null, // "male" / "female" / "other" / "prefer_not_to_say"
    val allergies: List<String> = emptyList(),
    val isPregnant: Boolean? = null,
    val chronicConditions: List<String> = emptyList(),
    val currentMedications: List<String> = emptyList()
)

enum class Role { PATIENT, DOCTOR }

enum class SubscriptionTier { NONE, PAY_PER_REVIEW, MONTHLY }

data class Subscription(
    val tier: SubscriptionTier = SubscriptionTier.NONE,
    val active: Boolean = false,
    val expiry: Date? = null
)
