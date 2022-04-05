package com.supdeweb.audiodb.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.supdeweb.audiodb.model.TitreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TitreDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(titre: TitreEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(titres: List<TitreEntity>)

    @Query("SELECT * FROM titres ORDER BY titres ASC")
    fun getTracks(): List<TitreEntity>

    @Query("SELECT * FROM titres WHERE id = :trackId")
    fun getTrackById(trackId: String): TitreEntity

    @Query("SELECT * FROM titres")
    fun getTracksFlow(): Flow<List<TitreEntity>>

    @Query("SELECT * FROM titres WHERE track_album_id = :albumId")
    fun getTracksByAlbumFlow(albumId: String): Flow<List<TitreEntity>>

    @Query("SELECT * FROM titres LIMIT 10")
    fun getFirstTenTracksFlow(): Flow<List<TitreEntity>>

    @Query("DELETE FROM titres WHERE id = :trackId")
    suspend fun deleteById(trackId: String)

    @Query("DELETE FROM titres")
    suspend fun deleteAll()
}