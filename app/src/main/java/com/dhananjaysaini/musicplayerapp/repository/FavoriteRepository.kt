package com.dhananjaysaini.musicplayerapp.repository

import com.dhananjaysaini.musicplayerapp.modal.FavoriteDao
import com.dhananjaysaini.musicplayerapp.modal.FavoriteEntity

class FavoriteRepository(private val dao: FavoriteDao) {

    suspend fun toggleFavorite(songId: String) {
        if (dao.isFavorite(songId)) {
            dao.removeFavorite(FavoriteEntity(songId))
        } else {
            dao.addFavorite(FavoriteEntity(songId))
        }
    }

    suspend fun getFavoriteIds(): List<String> {
        return dao.getAllFavorites().map { it.songId }
    }

    suspend fun isFavorite(songId: String): Boolean {
        return dao.isFavorite(songId)
    }
}
