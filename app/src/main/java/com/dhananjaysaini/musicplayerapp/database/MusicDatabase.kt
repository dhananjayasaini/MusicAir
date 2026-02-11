package com.dhananjaysaini.musicplayerapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dhananjaysaini.musicplayerapp.dao.FavouriteDao
import com.dhananjaysaini.musicplayerapp.dao.PlaylistDao
import com.dhananjaysaini.musicplayerapp.model.FavouriteEntity
import com.dhananjaysaini.musicplayerapp.model.PlaylistEntity
import com.dhananjaysaini.musicplayerapp.dao.PlaylistSongDao
import com.dhananjaysaini.musicplayerapp.model.PlaylistSongEntity

@Database(
    entities = [
        FavouriteEntity::class,
        PlaylistEntity::class,
        PlaylistSongEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun FavouriteDao(): FavouriteDao
    abstract fun PlaylistDao(): PlaylistDao
    abstract fun PlaylistSongDao(): PlaylistSongDao

    companion object {
        @Volatile private var INSTANCE: MusicDatabase? = null

        fun getDatabase(context: Context): MusicDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    "music_db"
                ).fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
