package com.supdeweb.audiodb.features

import com.supdeweb.audiodb.model.ArtistModel
import com.supdeweb.audiodb.repository.ArtistRepository
import com.supdeweb.audiodb.repository.Resource
import kotlinx.coroutines.flow.Flow

class GetArtisteFavorisFlow(
    private val artistRepo: ArtistRepository,
) {
    operator fun invoke(): Flow<Resource<List<ArtistModel>>> {
        return artistRepo.getFavoriteArtistsFlow()
    }
}