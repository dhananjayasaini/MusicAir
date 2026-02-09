package com.dhananjaysaini.musicplayerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val songId: Int = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
