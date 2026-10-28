package com.catsmoker.aimedicalagent.util

object EmergencyDetector {
    /** Client-side keyword detection for obvious emergencies. Used as a fast first line of defense. */
    private val EMERGENCY_KEYWORDS = listOf(
        "chest pain", "difficulty breathing", "trouble breathing", "shortness of breath",
        "cannot breathe", "can't breathe", "stroke", "face drooping", "arm weakness",
        "speech difficulty", "heavy bleeding", "uncontrolled bleeding", "coughing blood",
        "vomiting blood", "suicide", "suicidal", "kill myself", "end my life",
        "choking", "seizure", "loss of consciousness", "unconscious", "overdose",
        "allergic reaction", "anaphylaxis"
    )

    private val EMERGENCY_KEYWORDS_FR = listOf(
        "douleur thoracique", "difficulté à respirer", "étouffement", "accident vasculaire",
        "hémorragie", "saignement abondant", "suicide", "suicidaire", "mettre fin à mes jours",
        "crise", "perte de connaissance", "inconscient", "surdose", "réaction allergique"
    )

    private val EMERGENCY_KEYWORDS_AR = listOf(
        "ألم في الصدر", "صعوبة في التنفس", "نزيف حاد", "سكتة", "انتحار", "غيبوبة",
        "نوبة", "تسمم", "رد فعل تحسسي"
    )

    fun isEmergency(text: String, language: String): Boolean {
        val t = text.lowercase()
        val keywords = when (language) {
            "fr" -> EMERGENCY_KEYWORDS_FR
            "ar" -> EMERGENCY_KEYWORDS_AR
            else -> EMERGENCY_KEYWORDS
        }
        return keywords.any { t.contains(it) }
    }
}
