package com.dhananjaysaini.musicplayerapp.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.databinding.ActivityPlayerBinding
import com.dhananjaysaini.musicplayerapp.model.formatDuration
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.utils.VoiceControlManager
import com.dhananjaysaini.musicplayerapp.viewmodel.FavouriteViewModel
import com.dhananjaysaini.musicplayerapp.viewmodel.PlayerViewModel


class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var voiceControl: VoiceControlManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (MusicService.playlist.isEmpty()) {
            finish()
            return
        }
        observeViewModel()
        setupClicks()
        checkAudioPermission()
        voiceControllerPa()

        sendBroadcast(Intent("REQUEST_UI_UPDATE"))

    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->

            val song = state.currentSong ?: return@observe

            binding.songNamePA.text = song.title
            binding.songAlbumPA.text = song.album
            binding.startTimePA.text = formatDuration(state.positionMs.toLong())
            binding.endTimePA.text = formatDuration(state.durationMs.toLong())

            Glide.with(this)
                .load(song.artUri)
                .apply(RequestOptions().placeholder(R.drawable.itunes))
                .into(binding.songImgPA)

            binding.seekBarPA.progress = state.positionMs
            binding.seekBarPA.max = state.durationMs

            binding.playPauseBtnPA.setImageResource(
                if (state.isPlaying) R.drawable.pause_icon
                else R.drawable.play_icon
            )

            binding.favouriteBtnPA.setImageResource(
                if (state.isFavourite) R.drawable.favourite_filled_icon
                else R.drawable.favourite_empty_icon
            )

            binding.repeatBtnPA.setImageResource(
                if (state.isRepeat) R.drawable.repeat_one_icon
                else R.drawable.repeat_icon
            )
        }
    }

    private fun setupClicks() {

        binding.playPauseBtnPA.setOnClickListener {
            viewModel.playPause()
        }

        binding.nextBtnPA.setOnClickListener {
            viewModel.nextSong()
        }

        binding.prevBtnPA.setOnClickListener {
            viewModel.previousSong()
        }

        binding.favouriteBtnPA.setOnClickListener {
            viewModel.toggleFavourite()

            val song = MusicService.playlist
                .getOrNull(MusicService.position)
                ?: return@setOnClickListener

            // 🔥 SAME ViewModel jo Fragment use kar raha hai
                 ViewModelProvider(this)
                .get(FavouriteViewModel::class.java)
                .toggle(song.id)
        }

        binding.repeatBtnPA.setOnClickListener {
            viewModel.toggleRepeat()
        }

        binding.seekBarPA.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, p: Int, f: Boolean) {
                    if (f) viewModel.seekTo(p)
                }

                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            }
        )

        binding.backBtnPA.setOnClickListener { finish() }
    }

    private fun voiceControllerPa() {
        voiceControl = VoiceControlManager(this)
        binding.voiceControlPa.btnMic.setOnClickListener {
            voiceControl.startListening()
        }
    }

    private fun checkAudioPermission() {
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 101)
        }

    }
}

