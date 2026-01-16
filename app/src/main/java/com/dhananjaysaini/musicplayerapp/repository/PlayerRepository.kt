package com.dhananjaysaini.musicplayerapp.repository

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.utils.FavouriteManager

class PlayerRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun sendCommand(action: String, extras: Intent.() -> Unit = {}) {
        val intent = Intent(context, MusicService::class.java).apply {
            this.action = action
            extras()
        }
        context.startService(intent)
    }

    fun toggleFavourite(song: Music): Boolean {
        return FavouriteManager.toggleFavourite(song)
    }

    fun isFavourite(song: Music): Boolean {
        return FavouriteManager.isFavourite(song)
    }

    fun saveRepeatState(isRepeat: Boolean) {
        prefs.edit().putBoolean("isRepeat", isRepeat).apply()
    }

    fun loadRepeatState(): Boolean {
        return prefs.getBoolean("isRepeat", false)
    }
}
