package com.catsmoker.aimedicalagent.di

import com.catsmoker.aimedicalagent.data.repository.AiRepositoryImpl
import com.catsmoker.aimedicalagent.data.repository.AuthRepositoryImpl
import com.catsmoker.aimedicalagent.data.repository.HealthDataRepositoryImpl
import com.catsmoker.aimedicalagent.data.repository.LocaleRepositoryImpl
import com.catsmoker.aimedicalagent.data.repository.MedicalRepositoryImpl
import com.catsmoker.aimedicalagent.data.repository.PaymentRepositoryMock
import com.catsmoker.aimedicalagent.domain.repository.AiRepository
import com.catsmoker.aimedicalagent.domain.repository.AuthRepository
import com.catsmoker.aimedicalagent.domain.repository.HealthDataRepository
import com.catsmoker.aimedicalagent.domain.repository.LocaleRepository
import com.catsmoker.aimedicalagent.domain.repository.MedicalRepository
import com.catsmoker.aimedicalagent.domain.repository.PaymentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMedicalRepository(impl: MedicalRepositoryImpl): MedicalRepository = impl

    @Provides
    @Singleton
    fun provideAiRepository(impl: AiRepositoryImpl): AiRepository = impl

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun providePaymentRepository(impl: PaymentRepositoryMock): PaymentRepository = impl

    @Provides
    @Singleton
    fun provideLocaleRepository(impl: LocaleRepositoryImpl): LocaleRepository = impl

    @Provides
    @Singleton
    fun provideHealthDataRepository(impl: HealthDataRepositoryImpl): HealthDataRepository = impl
}
