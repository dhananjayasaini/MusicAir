package com.dhananjaysaini.musicplayerapp.utils

    import android.content.Context
    import android.content.SharedPreferences
    import com.dhananjaysaini.musicplayerapp.modal.Music
    import com.google.gson.Gson
    import com.google.gson.reflect.TypeToken

    /**
     * Handles adding/removing songs to favorites and persists them in SharedPreferences.
     */
    object FavoriteManager {

        private const val PREF_NAME = "favorites_pref"
        private const val KEY_FAVORITES = "favorite_songs"

        private val favoriteSongs = mutableListOf<Music>()
        private lateinit var prefs: SharedPreferences
        private val gson = Gson()

        /** Initialize in Application or first activity before using FavoriteManager */
        fun init(context: Context) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            loadFavorites()
        }

        /** Returns an immutable copy of favorites */
        fun getFavorites(): List<Music> = favoriteSongs.toList()

        /** Check if a song is marked as favorite */
        fun isFavorite(song: Music): Boolean {
            return favoriteSongs.any { it.id == song.id }
        }

        /**
         * Toggle favorite state for a song.
         * @return true if added, false if removed
         */
        fun toggleFavorite(song: Music): Boolean {
            val isFav = isFavorite(song)
            if (isFav) {
                favoriteSongs.removeAll { it.id == song.id }
            } else {
                favoriteSongs.add(song)
            }
            saveFavorites()
            return !isFav
        }

        /** Add song explicitly */
        fun addFavorite(song: Music) {
            if (!isFavorite(song)) {
                favoriteSongs.add(song)
                saveFavorites()
            }
        }

        /** Remove song explicitly */
        fun removeFavorite(song: Music) {
            favoriteSongs.removeAll { it.id == song.id }
            saveFavorites()
        }

        /** Load favorites from SharedPreferences */
        private fun loadFavorites() {
            val json = prefs.getString(KEY_FAVORITES, null) ?: return
            val type = object : TypeToken<MutableList<Music>>() {}.type
            val list: MutableList<Music> = gson.fromJson(json, type)
            favoriteSongs.clear()
            favoriteSongs.addAll(list)
        }

        /** Save favorites to SharedPreferences */
        private fun saveFavorites() {
            val editor = prefs.edit()
            val json = gson.toJson(favoriteSongs)
            editor.putString(KEY_FAVORITES, json)
            editor.apply()
        }


}