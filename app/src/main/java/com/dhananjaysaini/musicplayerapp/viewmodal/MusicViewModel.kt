package com.dhananjaysaini.musicplayerapp.viewmodal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dhananjaysaini.musicplayerapp.activities.MainActivity.Companion.musicListMA
import com.dhananjaysaini.musicplayerapp.repository.LoadMusicUseCase
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.repository.MusicRepository
import com.dhananjaysaini.musicplayerapp.service.MusicService
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = MusicRepository(application)
    private val loadMusicUseCase = LoadMusicUseCase(repo)

    val musicListLiveData = MutableLiveData<ArrayList<Music>>()
    private val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()

    fun loadMusic() {
        isLoading.postValue(true)

        MusicService.playlist = musicListMA


        viewModelScope.launch {
            try {
                val songs = loadMusicUseCase()
                musicListLiveData.postValue(songs)
            } catch (e: Exception) {
                error.postValue(e.message.toString())
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}
