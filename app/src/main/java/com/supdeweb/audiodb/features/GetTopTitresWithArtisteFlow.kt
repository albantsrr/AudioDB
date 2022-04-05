package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.model.TitreModel
import com.supdeweb.audiodb.repository.ArtistRepository
import com.supdeweb.audiodb.repository.TitreRepository
import com.supdeweb.audiodb.repository.Resource
import com.supdeweb.audiodb.repository.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class GetTopTitresWithArtisteFlow(
    private val titreRepo: TitreRepository,
    private val artistRepo: ArtistRepository,
) {
    suspend operator fun invoke(artistId: String): Flow<Resource<List<TitreModel>?>> {
        val artist = artistRepo.getArtistById(artistId)
        val fetchedTracks = titreRepo.getTracksByArtistName(artist.name ?: "")
        return when (fetchedTracks.first().status) {
            Status.SUCCESS -> fetchedTracks
            Status.ERROR -> flowOf(Resource.error(fetchedTracks.first().message ?: "", null))
            Status.LOADING -> flowOf(Resource.loading(null))
        }
    }
}