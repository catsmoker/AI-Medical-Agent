package com.catsmoker.aimedicalagent.util

import com.catsmoker.aimedicalagent.util.EmergencyDetector
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class EmergencyDetectorTest {

    @Test
    fun `detects english emergency keywords`() {
        assertTrue(EmergencyDetector.isEmergency("I have chest pain and trouble breathing", "en"))
        assertTrue(EmergencyDetector.isEmergency("heavy bleeding won't stop", "en"))
        assertTrue(EmergencyDetector.isEmergency("suicidal thoughts", "en"))
        assertFalse(EmergencyDetector.isEmergency("I have a mild headache", "en"))
    }

    @Test
    fun `detects french emergency keywords`() {
        assertTrue(EmergencyDetector.isEmergency("douleur thoracique et hémorragie", "fr"))
        assertFalse(EmergencyDetector.isEmergency("mal de tête léger", "fr"))
    }

    @Test
    fun `detects arabic emergency keywords`() {
        assertTrue(EmergencyDetector.isEmergency("ألم في الصدر ونزيف حاد", "ar"))
        assertFalse(EmergencyDetector.isEmergency("صداع خفيف", "ar"))
    }

    @Test
    fun `short vague text is not flagged as emergency by keyword scan`() {
        assertFalse(EmergencyDetector.isEmergency("hi", "en"))
    }
}
