package com.dhananjaysaini.musicplayerapp.modal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_songs")
data class FavoriteEntity(
    @PrimaryKey val songId: String
)
