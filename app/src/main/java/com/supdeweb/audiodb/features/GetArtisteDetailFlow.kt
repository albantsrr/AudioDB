package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.model.ArtistModel
import com.supdeweb.audiodb.model.asEntity
import com.supdeweb.audiodb.repository.ArtistRepository
import com.supdeweb.audiodb.repository.Resource
import com.supdeweb.audiodb.repository.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class GetArtisteDetailFlow(
    private val artistRepo: ArtistRepository,
) {
    suspend operator fun invoke(artistId: String): Flow<Resource<ArtistModel?>> {
        val fetchedAlbum = artistRepo.fetchArtistDetailBy(artistId).first()
        return when (fetchedAlbum.status) {
            Status.SUCCESS -> {
                fetchedAlbum.data?.asEntity()?.let { artistRepo.insertArtist(it) }
                return artistRepo.getArtistByIdFlow(artistId)
            }
            Status.ERROR -> flowOf(Resource.error(fetchedAlbum.message ?: "", null))
            Status.LOADING -> flowOf(Resource.loading(null))
        }
    }
}