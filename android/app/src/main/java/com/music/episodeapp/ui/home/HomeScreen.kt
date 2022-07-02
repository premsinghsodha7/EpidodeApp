package com.music.episodeapp.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Search
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.music.episodeapp.domain.model.Episode
import com.music.episodeapp.ui.common.PreviewContent
import com.music.episodeapp.ui.common.StaggeredVerticalGrid
import com.music.episodeapp.ui.common.ViewModelProvider
import com.music.episodeapp.ui.navigation.Destination
import com.music.episodeapp.ui.navigation.Navigator
import com.music.episodeapp.util.Resource
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen() {
    val scrollState = rememberLazyListState()
    val navController = Navigator.current
    val podcastSearchViewModel = ViewModelProvider.podcastSearch
    val podcastSearch = podcastSearchViewModel.podcastSearch

    Surface {
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        )
        LazyColumn(state = scrollState) {
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                ) {
                    SearchHere{
                        podcastSearchViewModel.searchPodcasts(it)
                    }
                }
            }

            item {
                LargeTitle()
            }

            when (podcastSearch) {
                is Resource.Error -> {
                    item {
                        ErrorView(text = podcastSearch.failure.translate()) {
                            podcastSearchViewModel.searchPodcasts()
                        }
                    }
                }
                Resource.Loading -> {
                    item {
                        LoadingPlaceholder()
                    }
                }
                is Resource.Success -> {
                    item {
                        StaggeredVerticalGrid(
                            crossAxisCount = 2,
                            spacing = 16.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            podcastSearch.data.results.forEach { podcast ->
                                PodcastView(
                                    podcast = podcast,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                ) {
                                    openPodcastDetail(navController, podcast)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 32.dp)
                        .padding(bottom = if (ViewModelProvider.podcastPlayer.currentPlayingEpisode.value != null) 64.dp else 0.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchHere(
    onSearchClick: (String) -> Unit
) {
    var searchState by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val textSize = TextUnit(
        value = 12F,
        type = TextUnitType.Sp
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(
                top = 32.dp,
                bottom = 8.dp
            )
            .height(48.dp),
        placeholder = {
                Text(
                    text = "Search here",
                    fontSize = textSize
                )
        },
        value = searchState,
        onValueChange = {
            searchState = it
        },
        textStyle = TextStyle(
            fontSize = textSize
        ),
        shape = RoundedCornerShape(50),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchClick.invoke(searchState)
                keyboardController?.hide()
            }
        ),
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .clickable {
                        onSearchClick.invoke(searchState)
                        keyboardController?.hide()
                    },
                imageVector = Icons.Sharp.Search,
                contentDescription = "Search Icon"
            )
        }
    )
}

private fun openPodcastDetail(
    navController: NavHostController,
    podcast: Episode
) {
    navController.navigate(Destination.podcast(podcast.id)) { }
}

@Composable
@Preview(name = "Home")
fun HomeScreenPreview() {
    PreviewContent {
        HomeScreen()
    }
}

@Composable
@Preview(name = "Home (Dark)")
fun HomeScreenDarkPreview() {
    PreviewContent(darkTheme = true) {
        HomeScreen()
    }
}