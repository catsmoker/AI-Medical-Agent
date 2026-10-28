package com.catsmoker.aimedicalagent.ui.screen

sealed class Screen {
    object Onboarding : Screen()
    object PatientChat : Screen()
    object History : Screen()
    object HealthProfile : Screen()
    object Upgrade : Screen()
    object Settings : Screen()
    object Language : Screen()
    object PrivacyPolicy : Screen()
    object EmergencyWarning : Screen()
    object DoctorQueue : Screen()
    data class DoctorCase(val caseId: String) : Screen()
}
