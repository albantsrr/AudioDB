package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.repository.ArtistRepository

class UpdateArtisteFavorisFlow(
    private val artistRepo: ArtistRepository,
) {
    suspend operator fun invoke(artistId: String, isFavorite: Boolean) {
        val artist = artistRepo.getArtistById(artistId)

        artistRepo.updateArtist(artist.copy(isFavorite = isFavorite))
    }
}