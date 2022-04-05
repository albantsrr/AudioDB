package com.supdeweb.audiodb.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.supdeweb.audiodb.room.TitreDao
import com.supdeweb.audiodb.room.AudioDBDatabase
import com.supdeweb.audiodb.model.dtoAsModel
import com.supdeweb.audiodb.model.entitiesAsModel
import com.supdeweb.audiodb.model.titreDtoAsModel
import com.supdeweb.audiodb.network.NetworkManager.titreService
import com.supdeweb.audiodb.network.service.TitreService
import com.supdeweb.audiodb.network.service.TrackListResponse
import com.supdeweb.audiodb.network.service.TrendingResponse
import com.supdeweb.audiodb.model.TitreEntity
import com.supdeweb.audiodb.model.TitreModel
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
class TitreRepository(
    private val titreDao: TitreDao,
    private val titreService: TitreService,
) {

    fun getTracksByAlbumFlow(albumId: String): Flow<Resource<List<TitreModel>>> {
        return titreDao.getTracksByAlbumFlow(albumId)
            .onStart { Resource.loading(null) }
            .map { Resource.success(it.entitiesAsModel()) }
    }

    @WorkerThread
    suspend fun insertAllTracks(titres: List<TitreEntity>) {
        titreDao.insertAll(titres)
    }

    fun getTrendingTracks(resource: (Resource<List<TitreModel>?>) -> Unit) {
        titreService.getTrendingTracks().enqueue(object : Callback<TrendingResponse> {
            override fun onResponse(
                call: Call<TrendingResponse>,
                response: Response<TrendingResponse>,
            ) {
                return resource(Resource.success(response.body()?.trending?.titreDtoAsModel()))
            }

            override fun onFailure(call: Call<TrendingResponse>, t: Throwable) {
                return resource(Resource.error(t.message ?: "Cannot fetch trending titres", null))
            }
        })
    }

    fun getTracksBy(albumId: String): Flow<Resource<List<TitreModel>?>> {
        return callbackFlow {
            titreService.getTracksByAlbum(albumId).enqueue(object : Callback<TrackListResponse> {
                override fun onResponse(
                    call: Call<TrackListResponse>,
                    response: Response<TrackListResponse>,
                ) {
                    trySend(Resource.success(response.body()?.titres?.dtoAsModel()))
                }

                override fun onFailure(call: Call<TrackListResponse>, t: Throwable) {
                    trySend(Resource.error(t.message ?: "Cannot fetch titres by album", null))
                }
            })
            awaitClose { this.cancel() }
        }
    }

    fun getTracksByArtistName(artistName: String): Flow<Resource<List<TitreModel>?>> {
        return callbackFlow {
            titreService.getTopTracksByArtistName(artistName)
                .enqueue(object : Callback<TrackListResponse> {
                    override fun onResponse(
                        call: Call<TrackListResponse>,
                        response: Response<TrackListResponse>,
                    ) {
                        trySend(Resource.success(response.body()?.titres?.dtoAsModel()))
                    }

                    override fun onFailure(call: Call<TrackListResponse>, t: Throwable) {
                        trySend(Resource.error(t.message ?: "Cannot fetch titres by artiste name",
                            null))
                    }
                })
            awaitClose { this.cancel() }
        }
    }


    companion object {
        @Volatile
        var INSTANCE: TitreRepository? = null

        fun getInstance(context: Context): TitreRepository {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    val db: AudioDBDatabase =
                        AudioDBDatabase.getInstance(context)
                    val repo = TitreRepository(
                        db.titreDao,
                        titreService,
                    )
                    instance = repo
                    INSTANCE = instance
                }
                return instance
            }

        }
    }
}