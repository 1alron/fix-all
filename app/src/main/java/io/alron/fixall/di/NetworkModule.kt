package io.alron.fixall.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.alron.fixall.BuildConfig
import io.alron.fixall.data.remote.api.AuthApi
import io.alron.fixall.data.remote.api.BranchesApi
import io.alron.fixall.data.remote.authenticator.AuthAuthenticator
import io.alron.fixall.data.remote.interceptor.AuthInterceptor
import io.alron.fixall.data.repository.AuthRepositoryImpl
import io.alron.fixall.data.repository.BranchesRepositoryImpl
import io.alron.fixall.data.repository.TokenStorageRepositoryImpl
import io.alron.fixall.domain.AuthManager
import io.alron.fixall.domain.repository.AuthRepository
import io.alron.fixall.domain.repository.BranchesRepository
import io.alron.fixall.domain.repository.TokenStorageRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson() = Gson()

    @Provides
    @Singleton
    fun provideOkHttp(
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(authAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}