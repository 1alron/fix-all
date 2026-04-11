package io.alron.fixall.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.alron.fixall.data.remote.api.AppointmentsApi
import io.alron.fixall.data.remote.api.BranchesApi
import io.alron.fixall.data.remote.api.CarsApi
import io.alron.fixall.data.repository.AppointmentsRepositoryImpl
import io.alron.fixall.data.repository.BranchesRepositoryImpl
import io.alron.fixall.data.repository.CarsRepositoryImpl
import io.alron.fixall.domain.repository.AppointmentsRepository
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
    @Singleton
    fun providesBranchesRepository(
        branchesApi: BranchesApi
    ): BranchesRepository = BranchesRepositoryImpl(branchesApi)

    @Provides
    @Singleton
    fun providesCarsApi(
        retrofit: Retrofit
    ): CarsApi = retrofit.create(CarsApi::class.java)

    @Provides
    @Singleton
    fun providesCarsRepository(
        carsApi: CarsApi,
        @ApplicationContext context: Context,
        gson: Gson
    ): CarsRepository = CarsRepositoryImpl(carsApi, context, gson)

    @Provides
    @Singleton
    fun providesAppointmentsApi(
        retrofit: Retrofit
    ): AppointmentsApi = retrofit.create(AppointmentsApi::class.java)

    @Provides
    @Singleton
    fun providesAppointmentsRepository(
        appointmentsApi: AppointmentsApi
    ): AppointmentsRepository = AppointmentsRepositoryImpl(appointmentsApi)
}