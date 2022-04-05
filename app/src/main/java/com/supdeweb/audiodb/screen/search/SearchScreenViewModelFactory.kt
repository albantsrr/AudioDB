package com.supdeweb.audiodb.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supdeweb.audiodb.features.GetAlbumsWithArtistNameFlow
import com.supdeweb.audiodb.features.GetArtisteWithNameFlow


class SearchScreenViewModelFactory(
    private val getArtisteWithNameFlow: GetArtisteWithNameFlow,
    private val getAlbumsWithArtistNameFlow: GetAlbumsWithArtistNameFlow,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchScreenViewModel::class.java)) {
            return SearchScreenViewModel(
                getArtisteWithNameFlow,
                getAlbumsWithArtistNameFlow
            ) as T
        }
        throw IllegalArgumentException("Unknown SearchScreenViewModel class")
    }
}