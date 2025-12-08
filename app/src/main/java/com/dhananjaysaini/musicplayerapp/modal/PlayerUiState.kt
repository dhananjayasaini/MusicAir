package com.dhananjaysaini.musicplayerapp.modal


data class PlayerUiState(
    val currentSong: Music? = null,
    val positionMs: Int = 0,
    val durationMs: Int = 0,
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val isRepeat: Boolean = false
)
