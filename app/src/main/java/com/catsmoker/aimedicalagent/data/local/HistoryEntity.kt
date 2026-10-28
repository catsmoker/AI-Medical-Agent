package com.catsmoker.aimedicalagent.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "case_history")
data class HistoryEntity(
    @PrimaryKey val id: String,
    val patientId: String,
    val patientMessage: String,
    val photoUri: String?,
    val language: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long,
    val aiAssessmentJson: String? // serialized AiAssessment
)
