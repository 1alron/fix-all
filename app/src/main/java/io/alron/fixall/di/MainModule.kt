package io.alron.fixall.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.alron.fixall.data.remote.api.BranchesApi
import io.alron.fixall.data.remote.api.CarsApi
import io.alron.fixall.data.repository.BranchesRepositoryImpl
import io.alron.fixall.data.repository.CarsRepositoryImpl
import io.alron.fixall.domain.repository.BranchesRepository
import io.alron.fixall.domain.repository.CarsRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {
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
    fun providesCarsApi(
        retrofit: Retrofit
    ): CarsApi = retrofit.create(CarsApi::class.java)

    @Provides
    fun providesCarsRepository(
        carsApi: CarsApi
    ): CarsRepository = CarsRepositoryImpl(carsApi)
}