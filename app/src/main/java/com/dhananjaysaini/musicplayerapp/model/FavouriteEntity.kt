package com.dhananjaysaini.musicplayerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Favourite_songs")
data class FavouriteEntity(
    @PrimaryKey val songId: String
)
