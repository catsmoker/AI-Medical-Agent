package com.catsmoker.aimedicalagent.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM case_history ORDER BY createdAt DESC")
    fun all(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM case_history WHERE id = :id")
    fun byId(id: String): Flow<HistoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HistoryEntity)

    @Query("DELETE FROM case_history")
    suspend fun deleteAll()
}
