package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.repository.AlbumRepository

class UpdateAlbumFavoris(
    private val albumRepo: AlbumRepository,
) {
    suspend operator fun invoke(albumId: String, isFavorite: Boolean) {
        val album = albumRepo.getAlbumById(albumId)

        albumRepo.updateAlbum(album.copy(isFavorite = isFavorite))
    }
}