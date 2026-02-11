package com.dhananjaysaini.musicplayerapp.repository

class LoadMusicUseCase(private val repo: MusicRepository) {
    suspend operator fun invoke() = repo.fetchAllSongs()
}
