package com.dhananjaysaini.musicplayerapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dhananjaysaini.musicplayerapp.model.FavouriteEntity

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(fav: FavouriteEntity)

    @Delete
    suspend fun removeFavourite(fav: FavouriteEntity)

    @Query("SELECT * FROM Favourite_songs")
    suspend fun getAllFavourites(): List<FavouriteEntity>

    @Query("SELECT songId FROM Favourite_songs")
    fun getFavouriteIds(): LiveData<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM Favourite_songs WHERE songId=:id)")
    suspend fun isFavourite(id: String): Boolean
}
