package com.dhananjaysaini.musicplayerapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.dhananjaysaini.musicplayerapp.model.Music
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PlaylistManager {

    private const val PREF_NAME = "Playlist_pref"
    private const val KEY_PLAYLISTS = "playlists"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    // playlistName -> songs
    private val playlists = mutableMapOf<String, MutableList<Music>>()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        loadPlaylists()
    }

    fun getPlaylists(): Map<String, MutableList<Music>> {
        return playlists
    }

    fun createPlaylist(name: String) {
        if (!playlists.containsKey(name)) {
            playlists[name] = mutableListOf()
            savePlaylists()
        }
    }

    fun addSongToPlaylist(playlistName: String, song: Music) {

        val list = playlists[playlistName] ?: mutableListOf()

        if (!list.any { it.id == song.id }) {
            list.add(song)
        }

        playlists[playlistName] = list

        savePlaylists()
    }

    fun removeSongFromPlaylist(playlistName: String, song: Music) {

        playlists[playlistName]?.removeAll { it.id == song.id }

        savePlaylists()
    }

    private fun loadPlaylists() {

        val json = prefs.getString(KEY_PLAYLISTS, null) ?: return

        val type = object : TypeToken<MutableMap<String, MutableList<Music>>>() {}.type

        val map: MutableMap<String, MutableList<Music>> = gson.fromJson(json, type)

        playlists.clear()
        playlists.putAll(map)
    }

    private fun savePlaylists() {

        val json = gson.toJson(playlists)

        prefs.edit()
            .putString(KEY_PLAYLISTS, json)
            .apply()
    }
}