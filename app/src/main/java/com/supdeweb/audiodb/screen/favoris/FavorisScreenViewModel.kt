package com.supdeweb.audiodb.screen.favoris

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.model.ArtistModel
import com.supdeweb.audiodb.repository.Status
import com.supdeweb.audiodb.features.GetFavoriteAlbumsFlow
import com.supdeweb.audiodb.features.GetArtisteFavorisFlow
import com.supdeweb.audiodb.screen.StateEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val getFavoriteAlbumsFlow: GetFavoriteAlbumsFlow,
    private val getArtisteFavorisFlow: GetArtisteFavorisFlow,
) : ViewModel() {


    /**
     * albums
     */
    private val favoriteAlbumFlow = MutableStateFlow(
        FavoriteAlbumState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun albumState(): Flow<FavoriteAlbumState> {
        return favoriteAlbumFlow.asStateFlow()
    }

    /**
     * artists
     */
    private val favoriteArtistFlow = MutableStateFlow(
        FavoriteArtistState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun artistState(): Flow<FavoriteArtistState> {
        return favoriteArtistFlow.asStateFlow()
    }

    init {
        observeFavoriteAlbums()
        observeFavoriteArtists()
    }

    private fun observeFavoriteAlbums() {
        viewModelScope.launch {
            favoriteAlbumFlow.emit(
                FavoriteAlbumState(
                    currentStateEnum = StateEnum.LOADING,
                )
            )
            getFavoriteAlbumsFlow().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        favoriteAlbumFlow.emit(
                            FavoriteAlbumState(
                                currentStateEnum = StateEnum.SUCCESS,
                                albums = it.data
                            )
                        )
                    }
                    Status.ERROR -> {
                        favoriteAlbumFlow.emit(
                            FavoriteAlbumState(
                                currentStateEnum = StateEnum.ERROR,
                                errorMessage = it.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        favoriteAlbumFlow.emit(
                            FavoriteAlbumState(
                                currentStateEnum = StateEnum.LOADING,
                            )
                        )
                    }
                }
            }
        }
    }

    private fun observeFavoriteArtists() {
        viewModelScope.launch {
            favoriteArtistFlow.emit(
                FavoriteArtistState(
                    currentStateEnum = StateEnum.LOADING,
                )
            )
            getArtisteFavorisFlow().collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        favoriteArtistFlow.emit(
                            FavoriteArtistState(
                                currentStateEnum = StateEnum.SUCCESS,
                                artists = it.data
                            )
                        )
                    }
                    Status.ERROR -> {
                        favoriteArtistFlow.emit(
                            FavoriteArtistState(
                                currentStateEnum = StateEnum.ERROR,
                                errorMessage = it.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        favoriteArtistFlow.emit(
                            FavoriteArtistState(
                                currentStateEnum = StateEnum.LOADING,
                            )
                        )
                    }
                }
            }
        }
    }
}

data class FavoriteAlbumState(
    val currentStateEnum: StateEnum,
    val albums: List<AlbumModel>? = null,
    val errorMessage: String? = null,
)

data class FavoriteArtistState(
    val currentStateEnum: StateEnum,
    val artists: List<ArtistModel>? = null,
    val errorMessage: String? = null,
)