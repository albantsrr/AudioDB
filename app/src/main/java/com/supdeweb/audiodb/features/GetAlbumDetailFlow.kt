package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.model.asEntity
import com.supdeweb.audiodb.model.AlbumModel
import com.supdeweb.audiodb.repository.AlbumRepository
import com.supdeweb.audiodb.repository.Resource
import com.supdeweb.audiodb.repository.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class GetAlbumDetailFlow(
    private val albumRepo: AlbumRepository,
) {
    suspend operator fun invoke(albumId: String): Flow<Resource<AlbumModel?>> {
        val fetchedAlbum = albumRepo.getAlbumDetailByAlbumIdFlow(albumId).first()
        return when (fetchedAlbum.status) {
            Status.SUCCESS -> {
                fetchedAlbum.data?.asEntity()?.let { albumRepo.insertAlbum(it) }
                return albumRepo.getAlbumByIdFlow(albumId)
            }
            Status.ERROR -> flowOf(Resource.error(fetchedAlbum.message ?: "", null))
            Status.LOADING -> flowOf(Resource.loading(null))
        }
    }
}