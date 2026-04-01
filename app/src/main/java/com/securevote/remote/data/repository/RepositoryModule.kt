package com.securevote.remote.data.repository

import com.securevote.remote.data.api.SVRGatewayApi
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
    fun provideSVRRepository(api: SVRGatewayApi): SVRRepository {
        return SVRRepository(api)
    }
}
