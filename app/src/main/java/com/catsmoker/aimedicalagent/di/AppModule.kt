package com.catsmoker.aimedicalagent.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.catsmoker.aimedicalagent.config.AiConfig
import com.catsmoker.aimedicalagent.data.local.AppDatabase
import com.catsmoker.aimedicalagent.data.local.CaseDao
import com.catsmoker.aimedicalagent.data.local.HistoryLocalDataSource
import com.catsmoker.aimedicalagent.domain.repository.ProductLinkProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideCaseDao(database: AppDatabase): CaseDao {
        return database.caseDao()
    }

    @Provides
    @Singleton
    fun provideHistoryLocalDataSource(dao: CaseDao): HistoryLocalDataSource {
        return HistoryLocalDataSource(dao)
    }

    @Provides
    @Singleton
    fun provideEncryptedPrefs(@ApplicationContext context: Context): EncryptedSharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            "aimedicalagent_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    @Provides
    @Singleton
    fun provideProductLinkProvider(): ProductLinkProvider {
        return DefaultProductLinkProvider()
    }

    @Provides
    @Singleton
    fun provideAiConfig(): AiConfig {
        return AiConfig
    }
}

class DefaultProductLinkProvider : ProductLinkProvider {
    override val defaultSites: List<String> = listOf("amazon.com")

    override fun searchUrl(productName: String, site: String): String {
        val encoded = java.net.URLEncoder.encode(productName, java.nio.charset.StandardCharsets.UTF_8)
        return when (site) {
            "amazon.com" -> "https://www.amazon.com/s?k=$encoded"
            else -> "https://www.google.com/search?q=$encoded+pharmacy"
        }
    }
}
