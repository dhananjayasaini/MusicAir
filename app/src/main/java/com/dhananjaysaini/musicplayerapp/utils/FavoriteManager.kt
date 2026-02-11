package com.dhananjaysaini.musicplayerapp.utils

    import android.content.Context
    import android.content.SharedPreferences
    import com.dhananjaysaini.musicplayerapp.model.Music
    import com.google.gson.Gson
    import com.google.gson.reflect.TypeToken

    /**
     * Handles adding/removing songs to Favourites and persists them in SharedPreferences.
     */
    object FavouriteManager {

        private const val PREF_NAME = "Favourites_pref"
        private const val KEY_FavouriteS = "Favourite_songs"

        private val FavouriteSongs = mutableListOf<Music>()
        private lateinit var prefs: SharedPreferences
        private val gson = Gson()

        /** Initialize in Application or first activity before using FavouriteManager */
        fun init(context: Context) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            loadFavourites()
        }

        /** Returns an immutable copy of Favourites */
        fun getFavourites(): List<Music> = FavouriteSongs.toList()

        /** Check if a song is marked as Favourite */
        fun isFavourite(song: Music): Boolean {
            return FavouriteSongs.any { it.id == song.id }
        }

        /**
         * Toggle Favourite state for a song.
         * @return true if added, false if removed
         */
        fun toggleFavourite(song: Music): Boolean {
            val isFav = isFavourite(song)
            if (isFav) {
                FavouriteSongs.removeAll { it.id == song.id }
            } else {
                FavouriteSongs.add(song)
            }
            saveFavourites()
            return !isFav
        }

        /** Add song explicitly */
        fun addFavourite(song: Music) {
            if (!isFavourite(song)) {
                FavouriteSongs.add(song)
                saveFavourites()
            }
        }

        /** Remove song explicitly */
        fun removeFavourite(song: Music) {
            FavouriteSongs.removeAll { it.id == song.id }
            saveFavourites()
        }

        /** Load Favourites from SharedPreferences */
        private fun loadFavourites() {
            val json = prefs.getString(KEY_FavouriteS, null) ?: return
            val type = object : TypeToken<MutableList<Music>>() {}.type
            val list: MutableList<Music> = gson.fromJson(json, type)
            FavouriteSongs.clear()
            FavouriteSongs.addAll(list)
        }

        /** Save Favourites to SharedPreferences */
        private fun saveFavourites() {
            val editor = prefs.edit()
            val json = gson.toJson(FavouriteSongs)
            editor.putString(KEY_FavouriteS, json)
            editor.apply()
        }


}