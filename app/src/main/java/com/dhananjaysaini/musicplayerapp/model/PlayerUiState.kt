package com.dhananjaysaini.musicplayerapp.model


data class PlayerUiState(
    val currentSong: Music? = null,
    val positionMs: Int = 0,
    val durationMs: Int = 0,
    val startTime: Int = 0,
    val endTime: Int = 0,
    val isPlaying: Boolean = false,
    val isFavourite: Boolean = false,
    val isRepeat: Boolean = false
)
