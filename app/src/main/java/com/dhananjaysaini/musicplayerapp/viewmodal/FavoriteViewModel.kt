package com.dhananjaysaini.musicplayerapp.viewmodal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dhananjaysaini.musicplayerapp.database.MusicDatabase
import com.dhananjaysaini.musicplayerapp.repository.FavoriteRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val repo =
        FavoriteRepository(MusicDatabase.getDatabase(application).favoriteDao())

    val favoriteIds = MutableLiveData<List<String>>()

    fun loadFavorites() {
        viewModelScope.launch {
            favoriteIds.postValue(repo.getFavoriteIds())
        }
    }

    fun toggle(songId: String) {
        viewModelScope.launch {
            repo.toggleFavorite(songId)
            loadFavorites()
        }
    }

    suspend fun isFavorite(songId: String): Boolean {
        return repo.isFavorite(songId)
    }
}


