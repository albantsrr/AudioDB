package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.model.modelAsEntity
import com.supdeweb.audiodb.model.TitreModel
import com.supdeweb.audiodb.repository.TitreRepository
import com.supdeweb.audiodb.repository.Resource
import com.supdeweb.audiodb.repository.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class GetTitresWithAlbumFlow(
    private val titreRepo: TitreRepository,
) {
    suspend operator fun invoke(albumId: String): Flow<Resource<List<TitreModel>>> {
        val fetchedTracks = titreRepo.getTracksBy(albumId).first()
        return when (fetchedTracks.status) {
            Status.SUCCESS -> {
                fetchedTracks.data?.modelAsEntity()?.let { titreRepo.insertAllTracks(it) }
                return titreRepo.getTracksByAlbumFlow(albumId)
            }
            Status.ERROR -> flowOf(Resource.error(fetchedTracks.message ?: "", null))
            Status.LOADING -> flowOf(Resource.loading(null))
        }
    }
}