package com.music.episodeapp.data.network.service

import com.music.episodeapp.data.network.constant.ListenNotesAPI
import com.music.episodeapp.data.network.model.PodcastSearchDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PodcastService {

    @GET(ListenNotesAPI.SEARCH)
    suspend fun searchPodcasts(
        @Query("q") query: String,
        @Query("type") type: String,
    ): PodcastSearchDto
}