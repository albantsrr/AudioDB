package com.supdeweb.audiodb.screen.classement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supdeweb.audiodb.repository.AlbumRepository


class AlbumViewModelFactory(
    private val albumRepo: AlbumRepository,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
            return AlbumViewModel(albumRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}