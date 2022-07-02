package com.music.episodeapp.domain.repository

import com.music.episodeapp.domain.model.PodcastSearch
import com.music.episodeapp.error.Failure
import com.music.episodeapp.util.Either

interface PodcastRepository {

    suspend fun searchPodcasts(
        query: String,
        type: String,
    ): Either<Failure, PodcastSearch>

    suspend fun searchPodcastsTimely(
        query: String,
        type: String,
    ): Either<Failure, PodcastSearch>
}