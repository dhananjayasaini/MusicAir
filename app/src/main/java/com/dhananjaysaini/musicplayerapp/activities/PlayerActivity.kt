package com.dhananjaysaini.musicplayerapp.activities

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.databinding.ActivityPlayerBinding
import com.dhananjaysaini.musicplayerapp.modal.Music

class PlayerActivity : AppCompatActivity() {

    companion object {
        var musicListPA = ArrayList<Music>()
        var songPosition : Int = 0
        var mediaPlayer: MediaPlayer? = null
    }

    private lateinit var binding: ActivityPlayerBinding
    private var isPlaying : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeLayout()
        setMusic()
    }

    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .placeholder(R.drawable.itunes)
            .into(binding.songImgPA)
        binding.songNamePA.text = musicListPA[songPosition].title
    }

    private fun playAudio() {
        mediaPlayer?.start()
        isPlaying = true
        binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        isPlaying = false
        binding.playPauseBtnPA.setImageResource(R.drawable.play_icon)
    }

    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
                createdMediaPlayer()
            }
            "MainActivity" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
                createdMediaPlayer()
            }
        }
    }

    private fun setMusic(){
        binding.playPauseBtnPA.setOnClickListener{
            if (isPlaying){
                pauseAudio()
            }
            else playAudio()
        }

        binding.previousBtnPA.setOnClickListener{ prevNextSong(false)}
        binding.nextBtnPA.setOnClickListener{ prevNextSong(true)}
    }

    private fun createdMediaPlayer(){
        runCatching {
            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(musicListPA[songPosition].path)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            isPlaying = true
            binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
        }

    }

    private fun prevNextSong(increment: Boolean){
        if (increment)
        {
            setSongPosition(true)
            setLayout()
            createdMediaPlayer()
        }
        else{
            setSongPosition(false)
            setLayout()
            createdMediaPlayer()
        }
    }

    private fun setSongPosition(increment: Boolean){
        if(increment) {
            if (musicListPA.size - 1 == songPosition)
                songPosition = 0
            else songPosition++
        }
            else{
                if (0== songPosition)
                    songPosition = musicListPA.size-1
            else songPosition--
        }
    }

}