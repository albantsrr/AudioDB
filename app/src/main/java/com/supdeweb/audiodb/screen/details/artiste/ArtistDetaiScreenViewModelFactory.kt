package com.supdeweb.audiodb.screen.details.artiste

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supdeweb.audiodb.features.GetAlbumsWithArtistFlow
import com.supdeweb.audiodb.features.GetArtisteDetailFlow
import com.supdeweb.audiodb.features.UpdateArtisteFavorisFlow
import com.supdeweb.audiodb.features.GetTopTitresWithArtisteFlow


class ArtistDetaiScreenViewModelFactory(
    private val artistId: String,
    private val getArtisteDetailFlow: GetArtisteDetailFlow,
    private val getAlbumsWithArtistFlow: GetAlbumsWithArtistFlow,
    private val getTopTitresWithArtisteFlow: GetTopTitresWithArtisteFlow,
    private val updateArtisteFavorisFlow: UpdateArtisteFavorisFlow,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtistDetailViewModel::class.java)) {
            return ArtistDetailViewModel(
                artistId = artistId,
                getArtisteDetailFlow = getArtisteDetailFlow,
                getAlbumsWithArtistFlow = getAlbumsWithArtistFlow,
                updateArtisteFavorisFlow = updateArtisteFavorisFlow,
                getTopTitresWithArtisteFlow = getTopTitresWithArtisteFlow
            ) as T
        }
        throw IllegalArgumentException("Unknown AlbumDetailViewModel class")
    }
}