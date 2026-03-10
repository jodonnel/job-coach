package com.jodonnel.jobcoach.di

import com.jodonnel.jobcoach.BuildConfig
import com.jodonnel.jobcoach.stt.GoogleSttEngine
import com.jodonnel.jobcoach.stt.SttEngine
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Named("openshift_endpoint")
    fun provideOpenShiftEndpoint(): String = BuildConfig.OPENSHIFT_ENDPOINT
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SttModule {

    @Binds
    @Singleton
    abstract fun bindSttEngine(impl: GoogleSttEngine): SttEngine
}
