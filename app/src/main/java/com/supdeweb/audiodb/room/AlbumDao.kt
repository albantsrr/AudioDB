package com.supdeweb.audiodb.room

import androidx.room.*
import com.supdeweb.audiodb.model.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(album: AlbumEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(albums: List<AlbumEntity>)

    @Query("SELECT * FROM album ORDER BY titres ASC")
    fun getAlbums(): List<AlbumEntity>

    @Query("SELECT * FROM album WHERE id = :albumId")
    suspend fun getAlbumById(albumId: String): AlbumEntity

    @Query("SELECT * FROM album")
    fun getAlbumsFlow(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM album WHERE album_artist_id = :artistId ORDER BY year")
    fun getAllAlbumsByArtistFlow(artistId: String): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM album WHERE id = :albumId")
    fun getAlbumByIdFlow(albumId: String): Flow<AlbumEntity>

    @Query("SELECT * FROM album WHERE is_favorite_album = 1")
    fun getFavoriteAlbumsFlow(): Flow<List<AlbumEntity>>

    @Update
    suspend fun update(album: AlbumEntity): Int

    @Query("DELETE FROM album WHERE id = :albumId")
    suspend fun deleteById(albumId: String)

    @Query("DELETE FROM album")
    suspend fun deleteAll()
}