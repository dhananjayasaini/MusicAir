package com.dhananjaysaini.musicplayerapp.repository

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.utils.FavoriteManager

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

    fun toggleFavorite(song: Music): Boolean {
        return FavoriteManager.toggleFavorite(song)
    }

    fun isFavorite(song: Music): Boolean {
        return FavoriteManager.isFavorite(song)
    }

    fun saveRepeatState(isRepeat: Boolean) {
        prefs.edit().putBoolean("isRepeat", isRepeat).apply()
    }

    fun loadRepeatState(): Boolean {
        return prefs.getBoolean("isRepeat", false)
    }
}
