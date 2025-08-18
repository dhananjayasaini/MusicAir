package com.dhananjaysaini.musicplayerapp.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewOutlineProvider
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.databinding.ActivityPlayerBinding
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.utils.FavoritesManager
import java.io.File


class PlayerActivity : AppCompatActivity() {

    companion object {
        var musicListPA = ArrayList<Music>()
        var songPosition : Int = 0
        var mediaPlayer: MediaPlayer? = null
        var isRepeat: Boolean = false
        var isUserSeeking = false
    }

    private lateinit var binding: ActivityPlayerBinding
    private var isPlaying : Boolean = false

    private lateinit var handler: Handler
    private lateinit var updateSeekBarRunnable: Runnable

    private lateinit var seekBar: SeekBar
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

         seekBar = binding.seekBarPA
         startTime = binding.startTimePA
         endTime = binding.endTimePA
         binding.songAlbumPA.isSelected = true


        initializeLayout()
        setMusic()
        backPress()
        repeatSong()
        loadRepeatState()
        favAudioSet()
        playerList()

        val intent = Intent(this, MusicService::class.java).apply {
            intent.putExtra("musicList", ArrayList(musicListPA))
            intent.putExtra("songPosition", songPosition)
        }

        ContextCompat.startForegroundService(this, intent)

        Log.d("MUSIC_SERVICE", "Service started")

        binding.shareBtnPA.setOnClickListener {
            shareCurrentSong()
        }

        mediaPlayer?.let {
            seekBar.max = it.duration
            endTime.text = formatTime(it.duration)
        }

        handler = Handler(Looper.getMainLooper())

        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (!isUserSeeking && it.isPlaying) {
                        seekBar.progress = it.currentPosition
                        startTime.text = formatTime(it.currentPosition)
                    }
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateSeekBarRunnable)
        playingSeekBar()

        binding.songImgPA.outlineProvider = ViewOutlineProvider.BACKGROUND
        binding.songImgPA.clipToOutline = true

    }

    override fun onDestroy() {
        super.onDestroy()
      //  handler.removeCallbacks(updateSeekBarRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun playingSeekBar() {

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                    startTime.text = formatTime(progress)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
                isUserSeeking = true
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                isUserSeeking = false
                mediaPlayer?.seekTo(seekBar.progress ?: 0)
            }
        })
    }

    @SuppressLint("DefaultLocale")
    fun formatTime(ms: Int): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setLayout() {
        if (isDestroyed || isFinishing || musicListPA.isEmpty()) return

        try {
            binding.songNamePA.text = musicListPA[songPosition].title
            binding.songAlbumPA.text = musicListPA[songPosition].album


            musicListPA[songPosition]
            val song = musicListPA[songPosition]
            val isFav = FavoritesManager.isFavorite(song.id)
            binding.favoriteBtnPA.setImageResource(
                if (isFav) R.drawable.favourite_filled_icon else R.drawable.favourite_empty_icon
            )

            Glide.with(this@PlayerActivity.applicationContext)
                .load(musicListPA[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.itunes).centerCrop())
                .into(binding.songImgPA)

            binding.endTimePA.text = formatTime(mediaPlayer!!.duration)
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = mediaPlayer!!.duration
        }
        catch (e: Exception){
            e.printStackTrace()
        }
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
        val incomingPosition = intent.getIntExtra("index", 0)
        val incomingSource = intent.getStringExtra("class")

        when(incomingSource) {

            "MainActivity" -> {
                musicListPA.clear()
                musicListPA.addAll(MainActivity.musicListMA)
                musicListPA.shuffle()
            }

            "MusicAdapter" -> {
                if( musicListPA.isEmpty()) {
                    musicListPA.addAll(MainActivity.musicListMA)
                }
            }

            "FavouriteActivity" -> {
                musicListPA.clear()
                musicListPA.addAll(FavouriteActivity.favList)
            }

            "Notification" -> {
                songPosition = incomingPosition

                // Ensure musicListPA is not empty (in case activity was killed and recreated)
                if (musicListPA.isEmpty()) {
                    musicListPA.addAll(MainActivity.musicListMA)
                }
            }
        }

        if (musicListPA.isEmpty()) {
           // Toast.makeText(this, "No songs found to play", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (incomingPosition != songPosition || mediaPlayer == null) {
            songPosition = incomingPosition
            setLayout()
            createdMediaPlayer()

        } else {
            setLayout() // update UI with current song info
            if (mediaPlayer?.isPlaying == true) {
                binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
            } else {
                binding.playPauseBtnPA.setImageResource(R.drawable.play_icon)
            }
        }
    }

    private fun setMusic(){
        binding.playPauseBtnPA.setOnClickListener{
            if (mediaPlayer?.isPlaying == true){
                pauseAudio()
            }
            else playAudio()
        }

        binding.prevBtnPA.setOnClickListener{ prevNextSong(false)}
        binding.nextBtnPA.setOnClickListener{ prevNextSong(true)}
    }

    private fun createdMediaPlayer(){

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()

        runCatching {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(this, Uri.parse(musicListPA[songPosition].path))
            mediaPlayer?.prepare()
            mediaPlayer?.start()

            startService(Intent(this, MusicService::class.java))


            seekBar.max = mediaPlayer!!.duration
            endTime.text = formatTime(mediaPlayer!!.duration)

            mediaPlayer!!.isLooping = isRepeat

            playAudio()

            mediaPlayer?.setOnCompletionListener {
                Log.d("Next song", "Song finished. Checking repeat state.")
                if (!isFinishing && !isDestroyed) {
                    if (isRepeat) {
                        // Repeat current song
                        mediaPlayer?.seekTo(0)
                        mediaPlayer?.start()
                    } else {
                        // Loop to next song, and wrap if at the end
                        if (songPosition < musicListPA.size - 1) {
                            songPosition++
                        } else {
                            songPosition = 0 // loop to start
                        }
                        setLayout()
                        createdMediaPlayer()
                    }
                }
            }

        }
    }

    fun prevNextSong(increment: Boolean){
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

    private fun backPress(){
        binding.backBtnPA.setOnClickListener{
           // onBackPressedDispatcher.onBackPressed()
          onBackPressed()

        }
    }

    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            this,
            updateReceiver,
            IntentFilter("UPDATE_UI"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        ContextCompat.registerReceiver(this, songControlReceiver, IntentFilter().apply {
            addAction("ACTION_NEXT_SONG")
            addAction("ACTION_PREV_SONG")
        }, ContextCompat.RECEIVER_NOT_EXPORTED)


        ContextCompat.registerReceiver(
            this,
            updateReceiver,
            IntentFilter("UPDATE_UI"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        isPlaying = mediaPlayer?.isPlaying == true

        if (isPlaying) {
            binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
        } else {
            binding.playPauseBtnPA.setImageResource(R.drawable.play_icon)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(songControlReceiver)

       // unregisterReceiver(updateReceiver)
    }

    private val songControlReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_NEXT_SONG" -> prevNextSong(true)
                "ACTION_PREV_SONG" -> prevNextSong(false)
            }
        }
    }

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setLayout()
            playingSeekBar()
        }
    }

    private fun repeatSong(){

        binding.repeatBtnPA.setOnClickListener {
            isRepeat = !isRepeat
            mediaPlayer?.isLooping = isRepeat

            if (isRepeat) {
                binding.repeatBtnPA.setImageResource(R.drawable.repeat_one_icon)
                Toast.makeText(this, "Current Song", Toast.LENGTH_SHORT).show()
            } else {
                binding.repeatBtnPA.setImageResource(R.drawable.repeat_icon)
                Toast.makeText(this, "Loop All", Toast.LENGTH_SHORT).show()
            }

        }
        saveRepeatState()

    }

    private fun shareCurrentSong() {
        try {
            val song = musicListPA[songPosition]
            val songPath = musicListPA[songPosition].path
            val file = File(songPath)

            if (!file.exists()) {
                Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT).show()
                return
            }

            val uri: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Listen to this audio!")
                putExtra(Intent.EXTRA_TEXT, "Check out this audio:\nTitle: ${song.title}\nArtist: ${song.artist}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Share audio file via"))
        } catch (e: Exception) {
            Toast.makeText(this, "Error sharing file", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun loadRepeatState() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        isRepeat = prefs.getBoolean("isRepeat", false)
        mediaPlayer?.isLooping = isRepeat
    }

    private fun saveRepeatState() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        prefs.edit().putBoolean("isRepeat", isRepeat).apply()
    }

    private val uiUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getStringExtra("action")) {
                "playPause" -> updatePlayPauseIcon()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("MUSIC_PLAYER_UI_UPDATE")
        registerReceiver(uiUpdateReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(uiUpdateReceiver)
    }

    private fun updatePlayPauseIcon() {
        if (MusicService.mediaPlayer?.isPlaying == true) {
            binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
        }
        else {
            binding.playPauseBtnPA.setImageResource(R.drawable.play_icon)
        }
    }

    private fun favAudioSet(){
        binding.favoriteBtnPA.setOnClickListener {
            val song = musicListPA[songPosition]
            if (FavoritesManager.isFavorite(song.id)) {
                FavoritesManager.removeFavorite(song.id)
                binding.favoriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
            } else {
                FavoritesManager.addFavorite(song.id)
                binding.favoriteBtnPA.setImageResource(R.drawable.favourite_filled_icon)
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playerList(){
        val playlistName = "MyFavSongs"

        if (musicListPA.isEmpty()) {
            Toast.makeText(this, "Music list is empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (songPosition < 0 || songPosition >= musicListPA.size) {
            Toast.makeText(this, "Invalid song position", Toast.LENGTH_SHORT).show()
            return
        }

    }


}




