package com.supdeweb.audiodb.screen.classement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.repository.AlbumRepository
import com.supdeweb.audiodb.repository.Status
import com.supdeweb.audiodb.screen.StateEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlbumViewModel(private val albumRepo: AlbumRepository) : ViewModel() {

    private val albumsFlow = MutableStateFlow(
        AlbumState(
            currentStateEnum = StateEnum.IDLE,
        )
    )

    fun albumState(): Flow<AlbumState> {
        return albumsFlow.asStateFlow()
    }

    private val _toast = MutableStateFlow(String())
    fun toast(): Flow<String> {
        return _toast.asStateFlow()
    }

    init {
        getTrendingAlbums()
    }

    fun getTrendingAlbums() {
        viewModelScope.launch {
            val albumsRes = albumRepo.getTrendingAlbumsFlow().first()
            albumsFlow.emit(
                AlbumState(
                    currentStateEnum = StateEnum.LOADING,
                )
            )
            when (albumsRes.status) {
                Status.SUCCESS -> {
                    albumsFlow.emit(
                        AlbumState(
                            currentStateEnum = StateEnum.SUCCESS,
                            albums = albumsRes.data?.sortedBy { it.chartPlace }
                        )
                    )
                }
                Status.ERROR -> {
                    albumsFlow.emit(
                        AlbumState(
                            currentStateEnum = StateEnum.ERROR,
                            errorMessage = albumsRes.message
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

data class AlbumState(
    val currentStateEnum: StateEnum,
    val albums: List<AlbumModel>? = null,
    val errorMessage: String? = null,
)
