package com.dhananjaysaini.musicplayerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_history")
data class PlayHistoryEntity(

    @PrimaryKey
    val songId: String,

    val lastPlayed: Long = System.currentTimeMillis(),

    val playCount: Int = 1
)
