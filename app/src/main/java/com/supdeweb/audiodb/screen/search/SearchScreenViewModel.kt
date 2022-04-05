package com.supdeweb.audiodb.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.model.ArtistModel
import com.supdeweb.audiodb.repository.Status
import com.supdeweb.audiodb.features.GetAlbumsWithArtistNameFlow
import com.supdeweb.audiodb.features.GetArtisteWithNameFlow
import com.supdeweb.audiodb.screen.StateEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchScreenViewModel(
    private val getArtisteWithNameFlow: GetArtisteWithNameFlow,
    private val getAlbumsWithArtistNameFlow: GetAlbumsWithArtistNameFlow,
) : ViewModel() {

    /**
     * artists
     */
    private val artistsFlow = MutableStateFlow(
        ArtistState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun artistState(): Flow<ArtistState> {
        return artistsFlow.asStateFlow()
    }

    /**
     * albums
     */
    private val albumsFlow = MutableStateFlow(
        AlbumState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun albumState(): Flow<AlbumState> {
        return albumsFlow.asStateFlow()
    }


    fun observeArtistsByName(artistName: String) {
        viewModelScope.launch {
            artistsFlow.emit(
                ArtistState(
                    currentStateEnum = StateEnum.LOADING,
                )
            )
            getArtisteWithNameFlow(artistName).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        artistsFlow.emit(
                            ArtistState(
                                currentStateEnum = StateEnum.SUCCESS,
                                artists = it.data
                            )
                        )
                        observeAlbumsByArtist(artistName)
                    }
                    Status.ERROR -> {
                        artistsFlow.emit(
                            ArtistState(
                                currentStateEnum = StateEnum.ERROR,
                                errorMessage = it.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        artistsFlow.emit(
                            ArtistState(
                                currentStateEnum = StateEnum.LOADING,
                            )
                        )
                    }
                }
            }
        }
    }

    private fun observeAlbumsByArtist(artistName: String) {
        viewModelScope.launch {
            albumsFlow.emit(
                AlbumState(
                    currentStateEnum = StateEnum.LOADING,
                )
            )
            getAlbumsWithArtistNameFlow(artistName).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        albumsFlow.emit(
                            AlbumState(
                                currentStateEnum = StateEnum.SUCCESS,
                                albums = it.data
                            )
                        )
                    }
                    Status.ERROR -> {
                        albumsFlow.emit(
                            AlbumState(
                                currentStateEnum = StateEnum.ERROR,
                                errorMessage = it.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        albumsFlow.emit(
                            AlbumState(
                                currentStateEnum = StateEnum.LOADING,
                            )
                        )
                    }
                }
            }
        }
    }
}

data class ArtistState(
    val currentStateEnum: StateEnum,
    val artists: List<ArtistModel>? = null,
    val errorMessage: String? = null,
)

data class AlbumState(
    val currentStateEnum: StateEnum,
    val albums: List<AlbumModel>? = null,
    val errorMessage: String? = null,
)