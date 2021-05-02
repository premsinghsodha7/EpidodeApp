package com.fabirt.podcastapp.di

import com.fabirt.podcastapp.data.network.client.ListenNotesAPIClient
import com.fabirt.podcastapp.data.network.service.PodcastService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideHttpClient(): OkHttpClient = ListenNotesAPIClient.createHttpClient()

    @Provides
    @Singleton
    fun providePodcastService(
        client: OkHttpClient
    ): PodcastService = ListenNotesAPIClient.createPodcastService(client)
}