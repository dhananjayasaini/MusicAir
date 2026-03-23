package com.dhananjaysaini.musicplayerapp.utils

import com.dhananjaysaini.musicplayerapp.model.Music

object QueueManager {

    val queue = mutableListOf<Music>()
    var currentIndex = -1

    fun playNow(song: Music) {
        queue.clear()
        queue.add(song)
        currentIndex = 0
    }

    fun playNext(song: Music) {

        if (currentIndex == -1) {
            queue.add(song)
            currentIndex = 0
            return
        }

        queue.add(currentIndex + 1, song)
    }

    fun addToQueue(song: Music) {
        queue.add(song)
    }

    fun getCurrentSong(): Music? {
        return if (currentIndex in queue.indices) queue[currentIndex] else null
    }
}