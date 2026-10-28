import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("kotlin-kapt")
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

val properties = Properties()
val propertiesFile = rootProject.file("local.properties")
if (propertiesFile.exists()) {
    propertiesFile.inputStream().use { properties.load(it) }
}

android {
    namespace = "com.catsmoker.aimedicalagent"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.catsmoker.aimedicalagent"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Dev-only API key injected from local.properties (never committed).
        // TODO(production): move to Firebase AI Logic + App Check or a backend proxy.
        resValue("string", "gemini_api_key", properties.getProperty("GEMINI_API_KEY", "AIzaPlaceholder_TODO_replace_me"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Version-managed BOMs
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))

    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.graphics)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.animation)
    implementation(libs.compose.navigation)

    // Hilt DI
    implementation(libs.hilt.library)
    implementation(libs.hilt.navigation.compose.lib)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.gson)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Google AI / Gemini SDK
    implementation(libs.genai.sdk)

    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)

    // Play Billing
    implementation(libs.play.billing)

    // Security (EncryptedSharedPreferences)
    implementation(libs.security.crypto)

    // Debug / tooling
    debugImplementation(libs.compose.tooling)

    // Tests
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.espresso.core)
}

kotlin {
    jvmToolchain(21)
}
