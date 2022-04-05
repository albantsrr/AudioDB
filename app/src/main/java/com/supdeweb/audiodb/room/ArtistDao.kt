package com.supdeweb.audiodb.room

import androidx.room.*
import com.supdeweb.audiodb.model.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artist: ArtistEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(artists: List<ArtistEntity>)

    @Query("SELECT * FROM artiste WHERE id = :artistId")
    suspend fun getArtistById(artistId: String): ArtistEntity

    @Query("SELECT * FROM artiste")
    fun getAllArtistsFlow(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM artiste WHERE id = :artistId")
    fun getArtistByIdFlow(artistId: String): Flow<ArtistEntity>

    @Query("SELECT * FROM artiste WHERE is_favorite_artist = 1")
    fun getFavoriteArtistsFlow(): Flow<List<ArtistEntity>>

    @Update
    suspend fun update(artist: ArtistEntity): Int

    @Query("DELETE FROM artiste WHERE id = :artistId")
    suspend fun deleteById(artistId: String)

    @Query("DELETE FROM artiste")
    suspend fun deleteAll()
}