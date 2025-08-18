package com.dhananjaysaini.musicplayerapp.utils


import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import android.content.Context
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.google.gson.Gson

object PlaylistManager {
    private const val PREF_NAME = "playlists"
    private val gson = Gson()

    fun savePlaylist(context: Context, name: String, songs: List<Music>) {
        val json = gson.toJson(songs)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(name, json).apply()
    }

    fun getPlaylist(context: Context, name: String): List<Music> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(name, null) ?: return emptyList()
        val type = object : TypeToken<List<Music>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getAllPlaylistNames(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.all.keys
    }

    fun deletePlaylist(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(name).apply()
    }

    fun addToPlaylist(context: Context, playlistName: String, song: Music) {
        val currentList = getPlaylist(context, playlistName).toMutableList()
        if (currentList.none { it.id == song.id }) {
            currentList.add(song)
            savePlaylist(context, playlistName, currentList)
        }
    }
}
