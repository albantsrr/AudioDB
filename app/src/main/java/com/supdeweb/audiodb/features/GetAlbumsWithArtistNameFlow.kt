package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.repository.AlbumRepository
import com.supdeweb.audiodb.repository.Resource
import kotlinx.coroutines.flow.Flow

class GetAlbumsWithArtistNameFlow(
    private val albumRepo: AlbumRepository,
) {
    operator fun invoke(artistName: String): Flow<Resource<List<AlbumModel>?>> {
        return albumRepo.getAlbumsWithArtistNameFlow(artistName)
    }
}