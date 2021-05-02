package com.fabirt.podcastapp.data.service

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fabirt.podcastapp.constant.K
import com.fabirt.podcastapp.data.exoplayer.PodcastMediaSource
import com.fabirt.podcastapp.domain.model.Episode

@ExperimentalFoundationApi
@ExperimentalAnimationApi
class MediaPlayerServiceConnection(
    context: Context,
    private val mediaSource: PodcastMediaSource,
) {

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> get() = _playbackState

    private val _currentPlayingMedia = MutableLiveData<MediaMetadataCompat?>()
    val currentPlayingMedia: LiveData<MediaMetadataCompat?> get() = _currentPlayingMedia

    lateinit var mediaController: MediaControllerCompat

    private var isConnected: Boolean = false

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(context, MediaPlayerService::class.java),
        mediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }

    fun playPodcast(episodes: List<Episode>) {
        mediaSource.setEpisodes(episodes)
        mediaBrowser.sendCustomAction(K.START_MEDIA_PLAYBACK_ACTION, null, null)
    }

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    fun refreshMediaBrowserChildren() {
        mediaBrowser.sendCustomAction(K.REFRESH_MEDIA_BROWSER_CHILDREN, null, null)
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            isConnected = true
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            isConnected = false
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            isConnected = false
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            _currentPlayingMedia.postValue(metadata)
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}