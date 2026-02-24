package com.dhananjaysaini.musicplayerapp.repository

import com.dhananjaysaini.musicplayerapp.dao.PlayHistoryDao
import com.dhananjaysaini.musicplayerapp.model.PlayHistoryEntity

class PlayHistoryRepository(private val dao: PlayHistoryDao) {

    val recentSongs = dao.getRecentlyPlayed()
    val topSongs = dao.getTopPlayed()

    suspend fun updatePlay(songId: String) {

        val existing = dao.getSong(songId)

        if (existing == null) {

            dao.insert(
                PlayHistoryEntity(
                    songId = songId,
                    lastPlayed = System.currentTimeMillis(),
                    playCount = 1
                )
            )

        } else {

            dao.insert(
                existing.copy(
                    lastPlayed = System.currentTimeMillis(),
                    playCount = existing.playCount + 1
                )
            )
        }
    }
}
