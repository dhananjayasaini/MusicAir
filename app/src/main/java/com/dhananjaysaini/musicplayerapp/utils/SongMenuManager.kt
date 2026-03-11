package com.dhananjaysaini.musicplayerapp.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.dhananjaysaini.musicplayerapp.model.Music


object SongMenuManager  {

    fun handleAddToPlaylist(fragmentManager: FragmentManager, song: Music) {
        PlaylistPickerBottomSheet(song).show(fragmentManager, "PlaylistPicker")
    }

    fun handleAddToFavourite(fragmentManager: FragmentManager, song: Music) {
        FavouriteManager.addFavourite(song)
    }

    fun handleRemoveFromFavourite(context: Context, song: Music) {
        FavouriteManager.removeFavourite(song)
    }

    fun handleRemoveFromPlaylist(context: Context, song: Music) {
        PlaylistManager.removeSongFromPlaylist("", song)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun handleDelete(context: Context, song: Music) {
        DeleteManager.deleteSong(context, song)
    }

    fun handleShare(context: Context, song: Music) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_STREAM, song)

        context.startActivity(
            Intent.createChooser(intent, "Share Song")
        )
    }
}