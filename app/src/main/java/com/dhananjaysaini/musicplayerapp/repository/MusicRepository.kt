package com.dhananjaysaini.musicplayerapp.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.dhananjaysaini.musicplayerapp.model.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MusicRepository(private val context: Context) {

    suspend fun fetchAllSongs(): ArrayList<Music> = withContext(Dispatchers.IO) {

        val audioList = ArrayList<Music>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, selection, null, sortOrder
        )

        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val album = cursor.getString(2)
                    val artist = cursor.getString(3)
                    val duration = cursor.getLong(4)
                    val path = cursor.getString(5)
                    val albumId = cursor.getLong(6).toString()

                    val artUri = Uri
                        .parse("content://media/external/audio/albumart/$albumId")
                        .toString()

                    val file = File(path)
                    if (file.exists()) {
                        audioList.add(
                            Music(id, title, album, artist, duration, path, artUri)
                        )
                    }

                } while (cursor.moveToNext())
            }
        }

        audioList
    }
}
