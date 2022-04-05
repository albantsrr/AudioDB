package com.supdeweb.audiodb.screen.details.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.model.TitreModel
import com.supdeweb.audiodb.repository.Status
import com.supdeweb.audiodb.features.GetAlbumDetailFlow
import com.supdeweb.audiodb.features.UpdateAlbumFavoris
import com.supdeweb.audiodb.features.GetTitresWithAlbumFlow
import com.supdeweb.audiodb.screen.StateEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    val albumId: String,
    private val getTitresWithAlbumFlow: GetTitresWithAlbumFlow,
    private val getAlbumDetailFlow: GetAlbumDetailFlow,
    private val updateAlbumFavoris: UpdateAlbumFavoris,
) : ViewModel() {

    private val errorMessage = MutableStateFlow<String?>(null)
    fun errorMessageState(): Flow<String?> {
        return errorMessage.asStateFlow()
    }

    private val albumFlow = MutableStateFlow(
        AlbumDetailState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun albumState(): Flow<AlbumDetailState> {
        return albumFlow.asStateFlow()
    }

    private val trackListFlow = MutableStateFlow(
        TrackListState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun trackListState(): Flow<TrackListState> {
        return trackListFlow.asStateFlow()
    }

    init {
        refreshData()
    }

    fun refreshData() {
        observeAlbumDetail()
        observeTrackList()
    }

    private fun observeTrackList() {
        viewModelScope.launch {
            trackListFlow.emit(
                TrackListState(
                    currentStateEnum = StateEnum.LOADING,
                )
            )
            getTitresWithAlbumFlow(albumId).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        trackListFlow.emit(
                            TrackListState(
                                currentStateEnum = StateEnum.SUCCESS,
                                titres = it.data
                            )
                        )
                    }
                    Status.ERROR -> {
                        trackListFlow.emit(
                            TrackListState(
                                currentStateEnum = StateEnum.ERROR,
                                errorMessage = it.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        trackListFlow.emit(
                            TrackListState(
                                currentStateEnum = StateEnum.LOADING,
                            )
                        )
                    }
                }
            }

        }
    }

    private fun observeAlbumDetail() {
        viewModelScope.launch {
            albumFlow.emit(
                AlbumDetailState(
                    currentStateEnum = StateEnum.LOADING,
                )
            )
            getAlbumDetailFlow(albumId).collect {
                when (it.status) {
                    Status.SUCCESS -> {
                        albumFlow.emit(
                            AlbumDetailState(
                                currentStateEnum = StateEnum.SUCCESS,
                                album = it.data
                            )
                        )
                    }
                    Status.ERROR -> {
                        albumFlow.emit(
                            AlbumDetailState(
                                currentStateEnum = StateEnum.ERROR,
                                errorMessage = it.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        albumFlow.emit(
                            AlbumDetailState(
                                currentStateEnum = StateEnum.LOADING,
                            )
                        )
                    }
                }
            }
        }
    }


    fun updateFavoriteAlbum(isFavorite: Boolean) {
        viewModelScope.launch {
            updateAlbumFavoris(albumId, isFavorite)
        }
    }
}

data class AlbumDetailState(
    val currentStateEnum: StateEnum,
    val album: AlbumModel? = null,
    val errorMessage: String? = null,
)

data class TrackListState(
    val currentStateEnum: StateEnum,
    val titres: List<TitreModel>? = null,
    val errorMessage: String? = null,
)