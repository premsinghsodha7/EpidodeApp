package com.music.episodeapp.domain.repository

import com.music.episodeapp.data.datastore.PodcastDataStore
import com.music.episodeapp.data.network.service.PodcastService
import com.music.episodeapp.domain.model.PodcastSearch
import com.music.episodeapp.error.Failure
import com.music.episodeapp.util.Either
import com.music.episodeapp.util.left
import com.music.episodeapp.util.right

class PodcastRepositoryImpl(
    private val service: PodcastService,
    private val dataStore: PodcastDataStore
) : PodcastRepository {

    companion object {
        private const val TAG = "PodcastRepository"
    }

    override suspend fun searchPodcasts(
        query: String,
        type: String
    ): Either<Failure, PodcastSearch> {
        return try {
            val result = service.searchPodcasts(query, type).asDomainModel()
            dataStore.storePodcastSearchResult(result)
            right(result)
        } catch (e: Exception) {
            left(Failure.UnexpectedFailure)
        }
    }

    override suspend fun searchPodcastsTimely(
        query: String,
        type: String
    ): Either<Failure, PodcastSearch> {
        return try {
            val canFetchAPI = dataStore.canFetchAPI()
            if (canFetchAPI) {
                val result = service.searchPodcasts(query, type).asDomainModel()
                dataStore.storePodcastSearchResult(result)
                right(result)
            } else {
                right(dataStore.readLastPodcastSearchResult())
            }
        } catch (e: Exception) {
            left(Failure.UnexpectedFailure)
        }
    }
}