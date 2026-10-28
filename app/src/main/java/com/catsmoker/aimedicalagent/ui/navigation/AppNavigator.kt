package com.catsmoker.aimedicalagent.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object AppNavigator {
    @Composable
    fun rememberNavController(): NavHostController = androidx.navigation.compose.rememberNavController()
}

object Routes {
    const val ONBOARDING = "onboarding"
    const val PATIENT_CHAT = "patient_chat"
    const val HISTORY = "history"
    const val HEALTH_PROFILE = "health_profile"
    const val UPGRADE = "upgrade"
    const val SETTINGS = "settings"
    const val LANGUAGE = "language"
    const val PRIVACY_POLICY = "privacy_policy"
    const val EMERGENCY_WARNING = "emergency_warning"
    const val DOCTOR_QUEUE = "doctor_queue"
    const val DOCTOR_CASE = "doctor_case"
}

@Composable
fun AimNavHost(
    onboardingScreen: @Composable () -> Unit,
    patientChatScreen: @Composable () -> Unit,
    historyScreen: @Composable () -> Unit,
    healthProfileScreen: @Composable () -> Unit,
    upgradeScreen: @Composable () -> Unit,
    settingsScreen: @Composable () -> Unit,
    languageScreen: @Composable () -> Unit,
    privacyPolicyScreen: @Composable () -> Unit,
    emergencyWarningScreen: @Composable (() -> Unit) -> Unit,
    doctorQueueScreen: @Composable () -> Unit,
    doctorCaseScreen: @Composable (String) -> Unit,
    onNavigateToPatientChat: () -> Unit,
    onNavigateToDoctorQueue: () -> Unit,
    onNavigateToSettings: () -> Unit,
    initialRoute: String = Routes.ONBOARDING
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = initialRoute) {
        composable(Routes.ONBOARDING) { onboardingScreen() }

        composable(Routes.PATIENT_CHAT) { patientChatScreen() }

        composable(Routes.HISTORY) { historyScreen() }

        composable(Routes.HEALTH_PROFILE) { healthProfileScreen() }

        composable(Routes.UPGRADE) { upgradeScreen() }

        composable(Routes.SETTINGS) { settingsScreen() }

        composable(Routes.LANGUAGE) { languageScreen() }

        composable(Routes.PRIVACY_POLICY) { privacyPolicyScreen() }

        composable(
            Routes.EMERGENCY_WARNING,
            arguments = listOf(navArgument("onDismiss") { type = NavType.BoolType })
        ) {
            val onDismiss: () -> Unit = {
                navController.popBackStack()
            }
            emergencyWarningScreen(onDismiss)
        }

        composable(Routes.DOCTOR_QUEUE) { doctorQueueScreen() }

        composable(
            Routes.DOCTOR_CASE,
            arguments = listOf(navArgument("caseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: ""
            doctorCaseScreen(caseId)
        }
    }
}
