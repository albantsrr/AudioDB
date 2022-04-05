package com.supdeweb.audiodb.screen.details.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supdeweb.audiodb.features.GetAlbumDetailFlow
import com.supdeweb.audiodb.features.UpdateAlbumFavoris
import com.supdeweb.audiodb.features.GetTitresWithAlbumFlow


class AlbumDetailScreenViewModelFactory(
    private val albumId: String,
    private val getTitresWithAlbumFlow: GetTitresWithAlbumFlow,
    private val getAlbumDetailFlow: GetAlbumDetailFlow,
    private val updateAlbumFavoris: UpdateAlbumFavoris,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumDetailViewModel::class.java)) {
            return AlbumDetailViewModel(
                albumId,
                getTitresWithAlbumFlow,
                getAlbumDetailFlow,
                updateAlbumFavoris,
            ) as T
        }
        throw IllegalArgumentException("Unknown AlbumDetailViewModel class")
    }
}