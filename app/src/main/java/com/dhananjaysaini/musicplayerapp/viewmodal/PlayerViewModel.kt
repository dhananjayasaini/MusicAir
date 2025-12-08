package com.dhananjaysaini.musicplayerapp.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.modal.PlayerUiState
import com.dhananjaysaini.musicplayerapp.repository.PlayerRepository
import com.dhananjaysaini.musicplayerapp.service.MusicService

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PlayerRepository(application)
    private val handler = Handler(Looper.getMainLooper())

    val uiState = MutableLiveData<PlayerUiState>()

    var musicList = ArrayList<Music>()
    var songPosition = 0

    fun initializePlayer(list: ArrayList<Music>, position: Int) {
        musicList = list
        songPosition = position

        updateUiState()
        startSeekBarUpdates()
    }

    fun playPause() {
        val isPlaying = MusicService.mediaPlayer?.isPlaying == true
        repo.sendCommand(if (isPlaying) Constants.ACTION_PAUSE else Constants.ACTION_PLAY)
    }

    fun nextSong() {
        repo.sendCommand(Constants.ACTION_NEXT)
        moveToNextSong(true)
    }

    fun prevSong() {
        repo.sendCommand(Constants.ACTION_PREVIOUS)
        moveToNextSong(false)
    }

    fun seekTo(ms: Int) {
        repo.sendCommand(Constants.ACTION_SEEK_TO) {
            putExtra("seekToMs", ms)
        }
    }

    private fun moveToNextSong(next: Boolean) {
        if (next) {
            songPosition = (songPosition + 1) % musicList.size
        } else {
            songPosition = if (songPosition == 0) musicList.size - 1 else songPosition - 1
        }
        updateUiState()
    }

    fun toggleFavorite() {
        val current = musicList[songPosition]
        val fav = repo.toggleFavorite(current)
        updateUiState()
    }

    fun toggleRepeat() {
        val current = uiState.value ?: return
        val newRepeat = !current.isRepeat
        repo.saveRepeatState(newRepeat)
        updateUiState()
    }

    fun updateUiState() {
        val mp = MusicService.mediaPlayer
        val song = musicList.getOrNull(songPosition)

        uiState.postValue(
            PlayerUiState(
                currentSong = song,
                positionMs = mp?.currentPosition ?: 0,
                durationMs = mp?.duration ?: 0,
                isPlaying = mp?.isPlaying ?: false,
                isFavorite = song?.let { repo.isFavorite(it) } ?: false,
                isRepeat = repo.loadRepeatState()
            )
        )
    }

    private fun startSeekBarUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                updateUiState()
                handler.postDelayed(this, 1000)
            }
        })
    }
}
