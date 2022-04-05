package com.supdeweb.audiodb.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.supdeweb.audiodb.model.*
import com.supdeweb.audiodb.room.AlbumDao
import com.supdeweb.audiodb.room.AudioDBDatabase
import com.supdeweb.audiodb.network.NetworkManager.albumService
import com.supdeweb.audiodb.network.service.AlbumService
import com.supdeweb.audiodb.network.service.GetAlbumDetailResponse
import com.supdeweb.audiodb.network.service.TrendingResponse
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
class AlbumRepository(
    private val albumDao: AlbumDao,
    private val albumService: AlbumService,
) {

    fun getAlbumByIdFlow(albumId: String): Flow<Resource<AlbumModel>> {
        return albumDao.getAlbumByIdFlow(albumId)
            .onStart { Resource.loading(null) }
            .map { Resource.success(it.asModel()) }
    }

    fun getAllAlbumsByArtistFlow(artistId: String): Flow<Resource<List<AlbumModel>>> {
        return albumDao.getAllAlbumsByArtistFlow(artistId)
            .onStart { Resource.loading(null) }
            .map { Resource.success(it.entitiesAsModel()) }
    }

    fun getFavoriteAlbumsFlow(): Flow<Resource<List<AlbumModel>>> {
        return albumDao.getFavoriteAlbumsFlow()
            .onStart { Resource.loading(null) }
            .map { Resource.success(it.entitiesAsModel()) }
    }

    suspend fun getAlbumById(albumId: String): AlbumModel {
        return albumDao.getAlbumById(albumId).asModel()
    }

    @WorkerThread
    suspend fun insertAlbum(album: AlbumEntity) {
        albumDao.insert(album)
    }

    suspend fun updateAlbum(album: AlbumModel) {
        albumDao.update(album.asEntity())
    }

    fun getTrendingAlbumsFlow(): Flow<Resource<List<AlbumModel>?>> {
        return callbackFlow {
            albumService.getTrendingAlbums().enqueue(object : Callback<TrendingResponse> {
                override fun onResponse(
                    call: Call<TrendingResponse>,
                    response: Response<TrendingResponse>,
                ) {
                    trySend(Resource.success(response.body()?.trending?.albumDtoAsModel()))
                }

                override fun onFailure(call: Call<TrendingResponse>, t: Throwable) {
                    trySend(Resource.error(t.message ?: "Cannot fetch trending albums", null))
                }
            })
            awaitClose { this.cancel() }
        }
    }

    fun getAlbumDetailByAlbumIdFlow(albumId: String): Flow<Resource<AlbumModel?>> {
        return callbackFlow {
            albumService.getAlbumDetail(albumId).enqueue(object : Callback<GetAlbumDetailResponse> {
                override fun onResponse(
                    call: Call<GetAlbumDetailResponse>,
                    response: Response<GetAlbumDetailResponse>,
                ) {
                    trySend(Resource.success(response.body()?.album?.first()?.asModel()))
                }

                override fun onFailure(call: Call<GetAlbumDetailResponse>, t: Throwable) {
                    trySend(Resource.error(t.message ?: "Cannot fetch album detail", null))
                }
            })
            awaitClose { this.cancel() }
        }
    }

    fun getAlbumsWithArtistIdFlow(artistId: String): Flow<Resource<List<AlbumModel>?>> {
        return callbackFlow {
            albumService.getAlbumsByArtistId(artistId)
                .enqueue(object : Callback<GetAlbumDetailResponse> {
                    override fun onResponse(
                        call: Call<GetAlbumDetailResponse>,
                        response: Response<GetAlbumDetailResponse>,
                    ) {
                        trySend(Resource.success(response.body()?.album?.dtoAsModel()))
                    }

                    override fun onFailure(call: Call<GetAlbumDetailResponse>, t: Throwable) {
                        trySend(Resource.error(t.message ?: "Cannot fetch albums by artiste", null))
                    }
                })
            awaitClose { this.cancel() }
        }
    }

    fun getAlbumsWithArtistNameFlow(artistName: String): Flow<Resource<List<AlbumModel>?>> {
        return callbackFlow {
            albumService.getAlbumsByArtistName(artistName)
                .enqueue(object : Callback<GetAlbumDetailResponse> {
                    override fun onResponse(
                        call: Call<GetAlbumDetailResponse>,
                        response: Response<GetAlbumDetailResponse>,
                    ) {
                        trySend(Resource.success(response.body()?.album?.dtoAsModel()))
                    }

                    override fun onFailure(call: Call<GetAlbumDetailResponse>, t: Throwable) {
                        trySend(Resource.error(t.message ?: "Cannot fetch albums by artiste", null))
                    }
                })
            awaitClose { this.cancel() }
        }
    }


    companion object {
        @Volatile
        var INSTANCE: AlbumRepository? = null

        fun getInstance(context: Context): AlbumRepository {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    val db: AudioDBDatabase =
                        AudioDBDatabase.getInstance(context)
                    val repo = AlbumRepository(
                        db.albumDao,
                        albumService,
                    )
                    instance = repo
                    INSTANCE = instance
                }
                return instance
            }

        }
    }
}