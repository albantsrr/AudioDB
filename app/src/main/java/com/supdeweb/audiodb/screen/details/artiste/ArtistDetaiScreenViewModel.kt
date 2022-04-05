package com.supdeweb.audiodb.screen.details.artiste

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.model.ArtistModel
import com.supdeweb.audiodb.model.TitreModel
import com.supdeweb.audiodb.repository.Status
import com.supdeweb.audiodb.features.GetAlbumsWithArtistFlow
import com.supdeweb.audiodb.features.GetArtisteDetailFlow
import com.supdeweb.audiodb.features.UpdateArtisteFavorisFlow
import com.supdeweb.audiodb.features.GetTopTitresWithArtisteFlow
import com.supdeweb.audiodb.screen.StateEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ArtistDetailViewModel(
    val artistId: String,
    private val getArtisteDetailFlow: GetArtisteDetailFlow,
    private val getAlbumsWithArtistFlow: GetAlbumsWithArtistFlow,
    private val getTopTitresWithArtisteFlow: GetTopTitresWithArtisteFlow,
    private val updateArtisteFavorisFlow: UpdateArtisteFavorisFlow,
) : ViewModel() {

    /**
     * error message
     */
    private val errorMessage = MutableStateFlow<String?>(null)
    fun errorMessageState(): Flow<String?> {
        return errorMessage.asStateFlow()
    }

    /**
     * artiste
     */
    private val artistFlow = MutableStateFlow(
        ArtistDetailState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun artistState(): Flow<ArtistDetailState> {
        return artistFlow.asStateFlow()
    }

    /**
     * albums
     */
    private val albumsFlow = MutableStateFlow(
        AlbumsByArtistState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun albumsState(): Flow<AlbumsByArtistState> {
        return albumsFlow.asStateFlow()
    }

    /**
     * albums
     */
    private val tracksFlow = MutableStateFlow(
        TracksByArtistState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun tracksState(): Flow<TracksByArtistState> {
        return tracksFlow.asStateFlow()
    }

    init {
        refreshData()
    }

    fun refreshData() {
        observeArtistDetail()
        observeAlbumsByArtist()
    }

    /**
     *
     */
    private fun observeArtistDetail() {
        viewModelScope.launch {
            artistFlow.emit(
                ArtistDetailState(
                    currentStateEnum = StateEnum.LOADING,
                )
            )
            getArtisteDetailFlow(artistId).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        artistFlow.emit(
                            ArtistDetailState(
                                currentStateEnum = StateEnum.SUCCESS,
                                artist = it.data
                            )
                        )
                    }
                    Status.ERROR -> {
                        artistFlow.emit(
                            ArtistDetailState(
                                currentStateEnum = StateEnum.ERROR,
                                errorMessage = it.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        artistFlow.emit(
                            ArtistDetailState(
                                currentStateEnum = StateEnum.LOADING,
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     *
     */
    private fun observeAlbumsByArtist() {
        viewModelScope.launch {
            albumsFlow.emit(
                AlbumsByArtistState(
                    currentStateEnum = StateEnum.LOADING,
                )
            )
            getAlbumsWithArtistFlow(artistId).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        albumsFlow.emit(
                            AlbumsByArtistState(
                                currentStateEnum = StateEnum.SUCCESS,
                                albums = it.data
                            )
                        )
                        observeTopTracks()
                    }
                    Status.ERROR -> {
                        albumsFlow.emit(
                            AlbumsByArtistState(
                                currentStateEnum = StateEnum.ERROR,
                                errorMessage = it.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        albumsFlow.emit(
                            AlbumsByArtistState(
                                currentStateEnum = StateEnum.LOADING,
                            )
                        )
                    }
                }
            }
        }
    }

    private fun observeTopTracks() {
        viewModelScope.launch {
            tracksFlow.emit(
                TracksByArtistState(
                    currentStateEnum = StateEnum.LOADING,
                )
            )
            getTopTitresWithArtisteFlow(artistId).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        tracksFlow.emit(
                            TracksByArtistState(
                                currentStateEnum = StateEnum.SUCCESS,
                                titres = it.data
                            )
                        )
                    }
                    Status.ERROR -> {
                        tracksFlow.emit(
                            TracksByArtistState(
                                currentStateEnum = StateEnum.ERROR,
                                errorMessage = it.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        tracksFlow.emit(
                            TracksByArtistState(
                                currentStateEnum = StateEnum.LOADING,
                            )
                        )
                    }
                }
            }
        }
    }


    fun updateFavoriteArtist(isFavorite: Boolean) {
        viewModelScope.launch {
            updateArtisteFavorisFlow(artistId, isFavorite)
        }
    }


}

data class ArtistDetailState(
    val currentStateEnum: StateEnum,
    val artist: ArtistModel? = null,
    val errorMessage: String? = null,
)

data class AlbumsByArtistState(
    val currentStateEnum: StateEnum,
    val albums: List<AlbumModel>? = null,
    val errorMessage: String? = null,
)

data class TracksByArtistState(
    val currentStateEnum: StateEnum,
    val titres: List<TitreModel>? = null,
    val errorMessage: String? = null,
)