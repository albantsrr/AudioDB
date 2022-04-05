package com.supdeweb.audiodb.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.supdeweb.audiodb.model.*
import com.supdeweb.audiodb.network.NetworkManager.artisteService
import com.supdeweb.audiodb.network.service.ArtistDetailResponse
import com.supdeweb.audiodb.network.service.ArtisteService
import com.supdeweb.audiodb.room.ArtistDao
import com.supdeweb.audiodb.room.AudioDBDatabase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ArtistRepository(
    private val artistDao: ArtistDao,
    private val artisteService: ArtisteService,
) {

    fun getArtistByIdFlow(artistId: String): Flow<Resource<ArtistModel>> {
        return artistDao.getArtistByIdFlow(artistId)
            .onStart { Resource.loading(null) }
            .map { Resource.success(it.asModel()) }
    }

    fun getFavoriteArtistsFlow(): Flow<Resource<List<ArtistModel>>> {
        return artistDao.getFavoriteArtistsFlow()
            .onStart { Resource.loading(null) }
            .map { Resource.success(it.entitiesAsModel()) }
    }

    suspend fun getArtistById(artistId: String): ArtistModel {
        return artistDao.getArtistById(artistId).asModel()
    }

    @WorkerThread
    suspend fun insertArtist(artist: ArtistEntity) {
        artistDao.insert(artist)
    }

    suspend fun updateArtist(artist: ArtistModel) {
        artistDao.update(artist.asEntity())
    }


    fun fetchArtistDetailBy(artistId: String): Flow<Resource<ArtistModel?>> {
        return callbackFlow {
            artisteService.getArtistDetail(artistId)
                .enqueue(object : Callback<ArtistDetailResponse> {
                    override fun onResponse(
                        call: Call<ArtistDetailResponse>,
                        response: Response<ArtistDetailResponse>,
                    ) {
                        trySend(Resource.success(response.body()?.artists?.first()?.asModel()))
                    }

                    override fun onFailure(call: Call<ArtistDetailResponse>, t: Throwable) {
                        trySend(Resource.error(t.message ?: "Cannot fetch artiste detail", null))
                    }
                })
            awaitClose { this.cancel() }
        }
    }

    fun fetchArtistsByName(artistName: String): Flow<Resource<List<ArtistModel>?>> {
        return callbackFlow {
            artisteService.getArtistsByName(artistName)
                .enqueue(object : Callback<ArtistDetailResponse> {
                    override fun onResponse(
                        call: Call<ArtistDetailResponse>,
                        response: Response<ArtistDetailResponse>,
                    ) {
                        trySend(Resource.success(response.body()?.artists?.dtoAsModel()))
                    }

                    override fun onFailure(call: Call<ArtistDetailResponse>, t: Throwable) {
                        trySend(Resource.error(t.message ?: "Cannot fetch artists by name", null))
                    }
                })
            awaitClose { this.cancel() }
        }
    }


    companion object {
        @Volatile
        var INSTANCE: ArtistRepository? = null

        fun getInstance(context: Context): ArtistRepository {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    val db: AudioDBDatabase =
                        AudioDBDatabase.getInstance(context)
                    val repo = ArtistRepository(
                        db.artistDao,
                        artisteService
                    )
                    instance = repo
                    INSTANCE = instance
                }
                return instance
            }

        }
    }
}