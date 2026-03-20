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
    fun providesBranchesApi(
        retrofit: Retrofit
    ): BranchesApi = retrofit.create(BranchesApi::class.java)

    @Provides
    fun providesBranchesRepository(
        branchesApi: BranchesApi
    ): BranchesRepository = BranchesRepositoryImpl(branchesApi)

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(
        @Named("auth") okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthApi(
        @Named("auth") retrofit: Retrofit
    ): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorageRepository =
        TokenStorageRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideGson() = Gson()

    @Provides
    fun provideAuthRepository(
        @Named("auth") api: AuthApi,
        tokenStorageRepository: TokenStorageRepository,
        gson: Gson
    ): AuthRepository =
        AuthRepositoryImpl(api, tokenStorageRepository, gson)

    @Provides
    @Singleton
    fun provideAuthManager(
        repository: AuthRepository,
        tokenStorageRepository: TokenStorageRepository
    ): AuthManager = AuthManager(
        authRepository = repository,
        tokenStorageRepository = tokenStorageRepository
    )

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