# AGENTS.md — AI Medical Agent (Android)

Kotlin + Jetpack Compose (Material 3) medical-assistant app. Clean Architecture (MVVM + domain/data), Hilt DI,
Navigation Compose, Room local history, Retrofit to Gemini, Firebase (mocked), Play Billing (mocked).
Single module `app`, package `com.catsmoker.aimedicalagent`.

## Build — read before running anything

- **Gradle Kotlin DSL** with a version catalog at `gradle/libs.versions.toml`. Root: `build.gradle.kts`, `settings.gradle.kts`.
- **No `gradlew` script here.** `gradle/wrapper/gradle-wrapper.properties` is present; generate the wrapper with
  `gradle wrapper` (or Android Studio) before building.
- **No Java/Gradle/Android SDK in this environment** — code is written but cannot be compiled locally. Open in
  Android Studio (SDK API 36) to build/run.
- `compileSdk`/`targetSdk` = **36** (latest stable), `minSdk` = 24, Java 11 / Kotlin toolchain 11, Compose enabled.
- **API key is NOT hardcoded.** Create `local.properties` with `GEMINI_API_KEY=...` (dev only). It is read into a
  generated string resource `gemini_api_key`. TODO: move to Firebase AI Logic + App Check or a backend proxy before
  production; `local.properties` is gitignored.
- **Firebase:** place `google-services.json` (from Firebase console) in `app/`. Auth/Firestore/Storage/FCM are wired
  behind repository interfaces but mocked by default.
- **Play Billing:** mocked first; real purchases behind `PaymentRepository`. Configure products in Play Console.

## Source layout

```
app/src/main/java/com/catsmoker/aimedicalagent/
  AIMApplication.kt              # Hilt Application
  MainActivity.kt                # Compose host; onboarding gate -> NavHost
  config/ApiConfig.kt            # GEMINI_MODEL = "gemini-3.8-flash" (single change point) + system prompt
  di/                            # AppModule, NetworkModule, RepositoryModule (Hilt)
  domain/model/                  # AiAssessment, MedicalCase, UserProfile, enums
  domain/repository/             # interfaces: MedicalRepository, AiRepository, AuthRepository, PaymentRepository,
                                 # ProductLinkProvider, LocaleRepository
  domain/usecase/                # GetAiAssessment
  data/remote/GeminiService.kt   # Retrofit interface to generativelanguage.googleapis.com
  data/repository/               # impls: AiRepositoryImpl, MedicalRepositoryImpl, AuthRepositoryImpl (mock),
                                 # PaymentRepositoryMock, LocaleRepositoryImpl
  data/local/                    # Room (AppDatabase, CaseDao, HistoryEntity), EncryptedPrefs
  ui/                            # theme, navigation, patient, doctor, onboarding, settings, language,
                                 # healthprofile, upgrade, emergency, privacy screens + ViewModels
  util/EmergencyDetector.kt      # client-side emergency keyword detection (en/fr/ar)
app/src/main/res/
  values/strings.xml, values-fr/, values-ar/   # en + fr + ar, RTL
  values/themes.xml, colors.xml                # keep; Compose theme overrides in ui/theme/
  drawable/, mipmap/, xml/                     # launcher icon, backup/data-extraction rules
# res/layout/ is intentionally EMPTY (Compose-only; XML layouts removed)
```

## Key conventions / gotchas

- **Model name in one constant:** `config.AiConfig.MODEL` = `gemini-3.8-flash` (verified from Google Gemini docs as the
  recommended stable Flash model for new projects; `gemini-2.0-flash` is shut down).
- **Structured JSON AI output:** system prompt forces JSON shape
  `{possible_conditions, explanation, self_care_steps, otc_product_names, red_flags, urgency_level, follow_up_questions}`.
  OTC = generic names only, no doses, no prescription drugs.
- **Per-app locale:** en/fr/ar via `LocaleRepository` (EncryptedSharedPreferences) applied to `Configuration`. Arabic = RTL.
- **Emergency:** `util.EmergencyDetector` keyword scan + AI `urgency_level == EMERGENCY` → full-screen warning, product
  suggestions disabled, call-emergency intent.
- **No health-content logging.** Sensitive prefs encrypted (`EncryptedSharedPreferences`). "Delete all my data" wipes Room + prefs.
- **Role selection** (Patient/Doctor) saved at onboarding; doctor mode requires mocked license+document verification.
- Tests: JUnit 5 + Robolectric under `src/test/`. Add ViewModel + AI parser tests.

## Config you must do yourself

1. Generate `gradlew` (`gradle wrapper`) + install Android Studio/SDK (API 36).
2. `local.properties`: `sdk.dir=...` and `GEMINI_API_KEY=...` (dev only — TODO to backend/Firebase AI Logic).
3. Firebase project → `google-services.json` in `app/`, enable Auth/Firestore/Storage/FCM.
4. Google Play Console → configure in-app products/subscriptions for Play Billing.
5. Verify dependency versions in `gradle/libs.versions.toml` against Maven Central.
