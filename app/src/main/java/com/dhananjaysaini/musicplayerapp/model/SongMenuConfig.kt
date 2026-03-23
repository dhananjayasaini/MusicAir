package com.dhananjaysaini.musicplayerapp.model

data class SongMenuConfig(
    val play: Boolean,
    val playNext: Boolean,
    val addToQueue: Boolean,
    val delete: Boolean,
    val edit: Boolean,
    val addToFav: Boolean,
    val removeFromFav: Boolean,
    val addToPlaylist: Boolean,
    val share:Boolean
)