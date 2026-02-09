 package com.dhananjaysaini.musicplayerapp.model

import androidx.room.Entity

@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"]
)
data class PlaylistSongEntity(
    val playlistId: Int,
    val songId: String
)
