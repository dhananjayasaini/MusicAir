package com.dhananjaysaini.musicplayerapp.modal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteEntity)

    @Delete
    suspend fun removeFavorite(fav: FavoriteEntity)

    @Query("SELECT * FROM favorite_songs")
    suspend fun getAllFavorites(): List<FavoriteEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE songId=:id)")
    suspend fun isFavorite(id: String): Boolean
}
