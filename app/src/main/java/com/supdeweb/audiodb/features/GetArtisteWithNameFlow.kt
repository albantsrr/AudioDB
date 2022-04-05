package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.model.ArtistModel
import com.supdeweb.audiodb.repository.ArtistRepository
import com.supdeweb.audiodb.repository.Resource
import kotlinx.coroutines.flow.Flow

class GetArtisteWithNameFlow(
    private val artistRepo: ArtistRepository,
) {
    operator fun invoke(artistName: String): Flow<Resource<List<ArtistModel>?>> {
        return artistRepo.fetchArtistsByName(artistName)
    }
}