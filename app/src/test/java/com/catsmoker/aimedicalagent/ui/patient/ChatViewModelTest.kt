package com.catsmoker.aimedicalagent.ui.patient

import com.catsmoker.aimedicalagent.domain.model.AiAssessment
import com.catsmoker.aimedicalagent.domain.model.UrgencyLevel
import com.catsmoker.aimedicalagent.domain.model.UserProfile
import com.catsmoker.aimedicalagent.domain.repository.LocaleRepository
import com.catsmoker.aimedicalagent.domain.repository.MedicalRepository
import com.catsmoker.aimedicalagent.domain.repository.ProductLinkProvider
import com.catsmoker.aimedicalagent.domain.usecase.GetAiAssessment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runTest
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

private const val LANG = "en"

private class FakeAiRepository : com.catsmoker.aimedicalagent.domain.repository.AiRepository {
    var resultToReturn: AiAssessment = AiAssessment(explanation = "ok", urgencyLevel = UrgencyLevel.SELF_CARE)
    var isVagueResult: Boolean = false
    override suspend fun assess(message: String, imageBytes: ByteArray?, imageMimeType: String?,
        language: String, profile: UserProfile): AiAssessment = resultToReturn
    override fun assessStream(message: String, imageBytes: ByteArray?, imageMimeType: String?,
        language: String, profile: UserProfile): Flow<AiAssessment> = flowOf(resultToReturn)
    override val isVague: (String) -> Boolean = { isVagueResult }
}

private class FakeGetAiAssessment(private val repo: FakeAiRepository) : GetAiAssessment(repo) {
    override fun isVague(message: String): Boolean = repo.isVagueResult
}

private class FakeMedicalRepository : MedicalRepository {
    val createdCases: MutableList<com.catsmoker.aimedicalagent.domain.model.MedicalCase> = mutableListOf()
    override val cases: Flow<com.catsmoker.aimedicalagent.domain.model.MedicalCase?> get() = flowOf(null)
    override fun caseById(caseId: String): Flow<com.catsmoker.aimedicalagent.domain.model.MedicalCase?> = flowOf(null)
    override suspend fun createCase(patientId: String, message: String, photoUri: String?, language: String,
        aiAssessment: AiAssessment, profile: UserProfile): String {
        createdCases.add(com.catsmoker.aimedicalagent.domain.model.MedicalCase(
            patientId = patientId, patientMessage = message, language = language, aiAssessment = aiAssessment))
        return "case_${createdCases.size}"
    }
    override suspend fun approveCase(caseId: String, doctorId: String) = Unit
    override suspend fun correctCase(caseId: String, doctorId: String, corrected: AiAssessment, notes: String) = Unit
    override suspend fun requestMoreInfo(caseId: String, doctorId: String, question: String) = Unit
    override suspend fun sendProductLink(caseId: String, doctorId: String, productName: String) = Unit
    override suspend fun addCaseMessage(caseId: String, senderId: String, senderRole: String, text: String) = Unit
    override fun localHistory(): Flow<List<com.catsmoker.aimedicalagent.domain.model.MedicalCase>> = flowOf(emptyList())
    override suspend fun saveLocalHistory(case: com.catsmoker.aimedicalagent.domain.model.MedicalCase) = Unit
    override suspend fun deleteAllLocalData() = Unit
}

private class FakeLocaleRepository : LocaleRepository {
    override val currentLanguage: Flow<String> = flowOf(LANG)
    override fun setLanguage(language: String) = Unit
    override val supportedLanguages: List<String> = listOf("en", "fr", "ar")
}

private class FakeProductLinkProvider : ProductLinkProvider {
    override fun searchUrl(productName: String, site: String): String = "https://example.com/search?q=$productName"
    override val defaultSites: List<String> = listOf("example.com")
}

class ChatViewModelTest {

    @Test
    fun `vague input produces follow-up questions and no case is saved`() = runTest {
        val fakeRepo = FakeAiRepository().apply { isVagueResult = true }
        val medical = FakeMedicalRepository()
        val vm = ChatViewModel(
            getAiAssessment = FakeGetAiAssessment(fakeRepo),
            medicalRepository = medical,
            localeRepository = FakeLocaleRepository(),
            productLinkProvider = FakeProductLinkProvider()
        )
        vm.setMessage("hi")
        vm.sendMessage(null, null)
        val state = vm.uiState.first()
        assertTrue(state.latestAssessment?.followUpQuestions?.isNotEmpty() == true)
        assertTrue(medical.createdCases.isEmpty())
    }

    @Test
    fun `emergency input sets showingEmergency and does not save`() = runTest {
        val fakeRepo = FakeAiRepository().apply {
            resultToReturn = AiAssessment(explanation = "chest pain", urgencyLevel = UrgencyLevel.EMERGENCY)
            isVagueResult = false
        }
        val medical = FakeMedicalRepository()
        val vm = ChatViewModel(
            getAiAssessment = FakeGetAiAssessment(fakeRepo),
            medicalRepository = medical,
            localeRepository = FakeLocaleRepository(),
            productLinkProvider = FakeProductLinkProvider()
        )
        vm.setMessage("chest pain")
        vm.sendMessage(null, null)
        val state = vm.uiState.first()
        assertTrue(state.showingEmergency)
        assertTrue(medical.createdCases.isEmpty())
    }
}
