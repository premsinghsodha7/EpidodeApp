package com.music.episodeapp.ui.common

import androidx.compose.runtime.Composable
import com.music.episodeapp.ui.navigation.ProvideNavHostController
import com.music.episodeapp.ui.theme.PodcastAppTheme
import com.google.accompanist.insets.ProvideWindowInsets

@Composable
fun PreviewContent(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    PodcastAppTheme(darkTheme = darkTheme) {
        ProvideWindowInsets {
            ProvideNavHostController {
                content()
            }
        }
    }
}