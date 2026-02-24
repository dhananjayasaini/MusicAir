package com.dhananjaysaini.musicplayerapp.utils

import android.content.Context
import android.provider.MediaStore
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.model.SongFolder
import java.io.File

fun getMusicFolders(context: Context): List<SongFolder> {

    val folderMap = HashMap<String, MutableList<Music>>()

    val projection = arrayOf(
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DISPLAY_NAME
    )

    val selection = "${MediaStore.Audio.Media.IS_MUSIC}!=0"

    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        null,
        null
    )

    cursor?.use {

        val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

        while (it.moveToNext()) {

            val path = it.getString(dataIndex)
            val file = File(path)

            val folderPath = file.parent ?: continue
            val folderName = File(folderPath).name

            val song = Music(
                path = path,
                title = file.nameWithoutExtension,
                album = folderName,
                duration = 0L,
                artUri = null.toString(),
                artist = "",
                id = "",
                date = 0L
            )

            folderMap.getOrPut(folderPath) {
                mutableListOf()
            }.add(song)
        }
    }

    return folderMap.map { (path, songs) ->
        SongFolder(
            name = File(path).name,
            path = path,
            songCount = songs.size
        )
    }.sortedBy { it.name.lowercase() }
}
