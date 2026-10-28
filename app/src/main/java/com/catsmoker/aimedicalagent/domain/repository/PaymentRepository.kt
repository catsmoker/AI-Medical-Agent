package com.catsmoker.aimedicalagent.domain.repository

import com.catsmoker.aimedicalagent.domain.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PaymentRepository {
    val subscription: Flow<Subscription>

    /** Purchase a one-time doctor review for a case. */
    suspend fun purchaseDoctorReview(caseId: String)

    /** Subscribe to monthly plan (every question auto-sent to a doctor). */
    suspend fun subscribeMonthly()

    suspend fun restorePurchases()
}

/** Builds store/pharmacy search links from AI-returned product names (never AI-invented URLs). */
interface ProductLinkProvider {
    fun searchUrl(productName: String, site: String): String
    val defaultSites: List<String>
}

/** Language / locale handling with per-app override. */
interface LocaleRepository {
    val currentLanguage: StateFlow<String> // "en" | "fr" | "ar"
    fun setLanguage(language: String)
    val supportedLanguages: List<String>
}
