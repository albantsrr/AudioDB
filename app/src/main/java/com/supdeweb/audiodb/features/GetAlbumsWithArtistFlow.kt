package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.model.modelAsEntity
import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.repository.AlbumRepository
import com.supdeweb.audiodb.repository.Resource
import com.supdeweb.audiodb.repository.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class GetAlbumsWithArtistFlow(
    private val albumRepo: AlbumRepository,
) {
    suspend operator fun invoke(artist: String): Flow<Resource<List<AlbumModel>?>> {
        val fetchedAlbums = albumRepo.getAlbumsWithArtistIdFlow(artist).first()
        return when (fetchedAlbums.status) {
            Status.SUCCESS -> {
                fetchedAlbums.data?.modelAsEntity()?.let {
                    it.map { album ->
                        albumRepo.insertAlbum(album)
                    }
                }
                return albumRepo.getAllAlbumsByArtistFlow(artist)
            }
            Status.ERROR -> flowOf(Resource.error(fetchedAlbums.message ?: "", null))
            Status.LOADING -> flowOf(Resource.loading(null))
        }
    }
}