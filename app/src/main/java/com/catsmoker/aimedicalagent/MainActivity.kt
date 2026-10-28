package com.catsmoker.aimedicalagent

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.catsmoker.aimedicalagent.domain.model.Role
import com.catsmoker.aimedicalagent.ui.doctor.DoctorCaseScreen
import com.catsmoker.aimedicalagent.ui.doctor.DoctorQueueScreen
import com.catsmoker.aimedicalagent.ui.emergency.EmergencyWarningScreen
import com.catsmoker.aimedicalagent.ui.healthprofile.HealthProfileScreen
import com.catsmoker.aimedicalagent.ui.language.LanguageScreen
import com.catsmoker.aimedicalagent.ui.navigation.AimNavHost
import com.catsmoker.aimedicalagent.ui.navigation.AppNavigator
import com.catsmoker.aimedicalagent.ui.navigation.Routes
import com.catsmoker.aimedicalagent.ui.onboarding.OnboardingScreen
import com.catsmoker.aimedicalagent.ui.patient.PatientChatScreen
import com.catsmoker.aimedicalagent.ui.privacy.PrivacyPolicyScreen
import com.catsmoker.aimedicalagent.ui.settings.SettingsScreen
import com.catsmoker.aimedicalagent.ui.theme.AimTheme
import com.catsmoker.aimedicalagent.ui.upgrade.UpgradeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AimTheme {
                val prefs: SharedPreferences = getSharedPreferences("aimedicalagent_prefs", Context.MODE_PRIVATE)
                val onboardingDone = remember { mutableStateOf(prefs.getBoolean(PREF_ONBOARDING, false)) }
                val userRole = remember { mutableStateOf<Role?>(null) }

                if (!onboardingDone.value) {
                    OnboardingScreen(
                        viewModel = hiltViewModel(),
                        onRoleSelected = { role ->
                            userRole.value = role
                            with(prefs.edit()) {
                                putBoolean(PREF_ONBOARDING, true)
                                putString(PREF_ROLE, role.name)
                                apply()
                            }
                            onboardingDone.value = true
                        }
                    )
                } else {
                    val stored = prefs.getString(PREF_ROLE, Role.PATIENT.name) ?: Role.PATIENT.name
                    val role = try { Role.valueOf(stored) } catch (e: Exception) { Role.PATIENT }
                    AppScreen(role = role)
                }
            }
        }
    }

    companion object {
        private const val PREF_ONBOARDING = "onboarding_done"
        private const val PREF_ROLE = "user_role"
    }
}

@Composable
fun AppScreen(role: Role) {
    val navController = AppNavigator.rememberNavController()
    val emergencyShown = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (emergencyShown.value) {
        EmergencyWarningScreen(
            onDismiss = { emergencyShown.value = false },
            onCallEmergency = {
                val intent = Intent(Intent.ACTION_CALL)
                context.startActivity(intent)
            }
        )
        return
    }

    AimNavHost(
        onboardingScreen = { },
        patientChatScreen = {
            PatientChatScreen(viewModel = hiltViewModel())
        },
        historyScreen = { Text("History") },
        healthProfileScreen = {
            HealthProfileScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        },
        upgradeScreen = {
            UpgradeScreen(
                onSubscribe = { },
                onRestore = { },
                onBack = { navController.popBackStack() }
            )
        },
        settingsScreen = {
            SettingsScreen(
                viewModel = hiltViewModel(),
                onLanguageClick = { navController.navigate(Routes.LANGUAGE) },
                onUpgradeClick = { navController.navigate(Routes.UPGRADE) },
                onPrivacyClick = { navController.navigate(Routes.PRIVACY_POLICY) },
                onDeleteData = { },
                onSignOut = { }
            )
        },
        languageScreen = {
            LanguageScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        },
        privacyPolicyScreen = {
            PrivacyPolicyScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        },
        emergencyWarningScreen = { onDismiss ->
            emergencyShown.value = true
            onDismiss()
        },
        doctorQueueScreen = {
            DoctorQueueScreen(
                viewModel = hiltViewModel(),
                onCaseClick = { caseId -> navController.navigate("${Routes.DOCTOR_CASE}/$caseId") },
                onBack = {}
            )
        },
        doctorCaseScreen = { caseId ->
            DoctorCaseScreen(
                caseId = caseId,
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        },
        onNavigateToPatientChat = {},
        onNavigateToDoctorQueue = {},
        onNavigateToSettings = {},
        initialRoute = if (role == Role.DOCTOR) Routes.DOCTOR_QUEUE else Routes.PATIENT_CHAT
    )
}
