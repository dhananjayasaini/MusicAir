package com.dhananjaysaini.musicplayerapp.repository

import androidx.lifecycle.LiveData
import com.dhananjaysaini.musicplayerapp.dao.FavouriteDao
import com.dhananjaysaini.musicplayerapp.model.FavouriteEntity

class FavouriteRepository(private val dao: FavouriteDao) {

    val favouriteIds: LiveData<List<String>> = dao.getFavouriteIds()

    suspend fun toggleFavourite(songId: String) {
        if (dao.isFavourite(songId)) {
            dao.removeFavourite(FavouriteEntity(songId))
        } else {
            dao.addFavourite(FavouriteEntity(songId))
        }
    }

    suspend fun getFavouriteIds(): List<String> {
        return dao.getAllFavourites().map { it.songId }
    }

    suspend fun isFavourite(songId: String): Boolean {
        return dao.isFavourite(songId)
    }

}
