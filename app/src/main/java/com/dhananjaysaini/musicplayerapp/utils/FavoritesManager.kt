package com.dhananjaysaini.musicplayerapp.utils

import android.content.Context
import android.content.SharedPreferences

object FavoritesManager {
    private const val PREF_NAME = "favorites_pref"
    private const val KEY_FAVORITES = "favorite_song_ids"

    private lateinit var sharedPrefs: SharedPreferences
    val favoriteSongs = mutableSetOf<String>()

    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        loadFavorites()
    }

    private fun loadFavorites() {
        favoriteSongs.clear()
        favoriteSongs.addAll(sharedPrefs.getStringSet(KEY_FAVORITES, emptySet())!!)
    }

    fun addFavorite(id: String) {
        favoriteSongs.add(id)
        saveFavorites()
    }

    fun removeFavorite(id: String) {
        favoriteSongs.remove(id)
        saveFavorites()
    }

    private fun saveFavorites() {
        sharedPrefs.edit().putStringSet(KEY_FAVORITES, favoriteSongs).apply()
    }

    fun isFavorite(id: String): Boolean = favoriteSongs.contains(id)
}
