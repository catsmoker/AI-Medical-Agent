package com.catsmoker.aimedicalagent.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.catsmoker.aimedicalagent.domain.model.MedicalCase
import kotlinx.coroutines.flow.Flow

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun caseDao(): CaseDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aimedicalagent_history.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class HistoryLocalDataSource(private val dao: CaseDao) {
    fun all(): Flow<List<HistoryEntity>> = dao.all()

    suspend fun save(entity: HistoryEntity) = dao.insert(entity)

    suspend fun deleteAll() = dao.deleteAll()
}
