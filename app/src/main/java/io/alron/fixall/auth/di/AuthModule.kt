package io.alron.fixall.auth.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.alron.fixall.BuildConfig
import io.alron.fixall.auth.data.remote.api.AuthApi
import io.alron.fixall.auth.data.repository.LoginRepositoryImpl
import io.alron.fixall.auth.data.storage.TokenStorage
import io.alron.fixall.auth.data.storage.TokenStorageImpl
import io.alron.fixall.auth.domain.repository.LoginRepository
import io.alron.fixall.auth.domain.usecase.LoginUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage =
        TokenStorageImpl(context)

    @Provides
    fun provideLoginRepository(
        api: AuthApi,
        tokenStorage: TokenStorage
    ): LoginRepository =
        LoginRepositoryImpl(api, tokenStorage)

    @Provides
    fun provideLoginUseCase(repository: LoginRepository): LoginUseCase =
        LoginUseCase(repository)
}