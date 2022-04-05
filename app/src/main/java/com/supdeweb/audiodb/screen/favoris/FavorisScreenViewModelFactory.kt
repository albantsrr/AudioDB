package com.supdeweb.audiodb.screen.favoris

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supdeweb.audiodb.features.GetFavoriteAlbumsFlow
import com.supdeweb.audiodb.features.GetArtisteFavorisFlow


class FavorisScreenViewModelFactory(
    private val getFavoriteAlbumsFlow: GetFavoriteAlbumsFlow,
    private val getArtisteFavorisFlow: GetArtisteFavorisFlow,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(
                getFavoriteAlbumsFlow,
                getArtisteFavorisFlow,
            ) as T
        }
        throw IllegalArgumentException("Unknown FavoriteViewModel class")
    }
}