package com.supdeweb.audiodb.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.supdeweb.audiodb.model.AlbumEntity
import com.supdeweb.audiodb.model.ArtistEntity
import com.supdeweb.audiodb.model.TitreEntity


@Database(
    entities = [
        AlbumEntity::class,
        TitreEntity::class,
        ArtistEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AudioDBDatabase : RoomDatabase() {

    abstract val albumDao: AlbumDao
    abstract val titreDao: TitreDao
    abstract val artistDao: ArtistDao

    companion object {

        @Volatile
        private var INSTANCE: AudioDBDatabase? = null

        fun getInstance(context: Context): AudioDBDatabase {
            synchronized(this) {
                var instance =
                    INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AudioDBDatabase::class.java,
                        "audio_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}