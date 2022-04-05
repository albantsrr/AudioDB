package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.repository.AlbumRepository
import com.supdeweb.audiodb.repository.Resource
import kotlinx.coroutines.flow.Flow

class GetFavoriteAlbumsFlow(
    private val albumRepo: AlbumRepository,
) {
    operator fun invoke(): Flow<Resource<List<AlbumModel>>> {
        return albumRepo.getFavoriteAlbumsFlow()
    }
}