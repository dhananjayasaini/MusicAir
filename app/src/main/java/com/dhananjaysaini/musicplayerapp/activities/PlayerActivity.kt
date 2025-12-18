package com.dhananjaysaini.musicplayerapp.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewOutlineProvider
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.databinding.ActivityPlayerBinding
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.utils.FavoriteManager
import com.dhananjaysaini.musicplayerapp.viewmodal.FavoriteViewModel
import kotlinx.coroutines.launch
import java.io.File


class PlayerActivity : AppCompatActivity() {

    companion object {
        var musicListPA = ArrayList<Music>()
        var songPosition: Int = 0
        var isRepeat: Boolean = false
        var isUserSeeking = false
    }

    private lateinit var binding: ActivityPlayerBinding
    private var isPlaying: Boolean = false

    private lateinit var handler: Handler
    private lateinit var updateSeekBarRunnable: Runnable

    private lateinit var seekBar: SeekBar
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var voiceControl: VoiceControlManager
    private val favViewModel: FavoriteViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.O)
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
        setupUiCallbacks()
        playerList()

        checkAudioPermission()

        voiceControl = VoiceControlManager(this)
        binding.voiceControlPa.btnMic.setOnClickListener{
            voiceControl.startListening()
        }


        // After you’ve built musicListPA and songPosition
//        val serviceIntent = Intent(this, MusicService::class.java).apply {
//            action = Constants.ACTION_PLAY_NEW_LIST
//            putExtra("musicList", ArrayList(musicListPA)) // Music must be Serializable or Parcelable
//            putExtra("songPosition", songPosition)
//        }
//
//        startService(serviceIntent)

        Log.d("MUSIC_SERVICE", "Service started")

        safeStartServiceIfNeeded()

        MusicService.mediaPlayer?.let {
            seekBar.max = it.duration
            endTime.text = formatTime(it.duration)
        }
        updateSeekBar()

        binding.songImgPA.outlineProvider = ViewOutlineProvider.BACKGROUND
        binding.songImgPA.clipToOutline = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun safeStartServiceIfNeeded() {
        // Start service with playlist only when we launched PlayerActivity to start playback
        // i.e., incomingSource is MainActivity or MusicAdapter, OR mediaPlayer is null
        val incomingSource = intent.getStringExtra("class")

        if (incomingSource == "MainActivity" || incomingSource == "MusicAdapter" || incomingSource == "FavouriteActivity" ||
            MusicService.mediaPlayer == null
        ) {

            // only if we actually have a non-empty playlist to send
            if (musicListPA.isNotEmpty()) {
                try {
                    val serviceIntent = Intent(this, MusicService::class.java).apply {
                        action = Constants.ACTION_PLAY_NEW_LIST
                        putExtra("musicList", ArrayList(musicListPA))
                        putExtra("songPosition", songPosition)
                    }
                    startForegroundService(serviceIntent)
                } catch (e: Exception) {
                    Log.e("PlayerActivity", "start service failed: ${e.message}", e)
                }
            }
        } else {
            // Coming from MiniPlayer or service already running: don't restart playback.
            // Just sync UI with current service state
            setLayout()
        }
    }


    private fun setupUiCallbacks() {

        binding.playPauseBtnPA.setOnClickListener {
            val isPlaying = MusicService.mediaPlayer?.isPlaying == true

            startService(Intent(this, MusicService::class.java).apply {
                action = if (isPlaying) Constants.ACTION_PAUSE else Constants.ACTION_PLAY
            })
        }

        binding.nextBtnPA.setOnClickListener {
         //   updateFavoriteIcon()
            updateFavIcon()
            nextPrevSong(true)
            startService(Intent(this, MusicService::class.java).apply {
                action = Constants.ACTION_NEXT
            })
        }

        binding.prevBtnPA.setOnClickListener {
            updateFavIcon()
            nextPrevSong(false)
            startService(Intent(this, MusicService::class.java).apply {
                action = Constants.ACTION_PREVIOUS
            })
        }

        binding.shareBtnPA.setOnClickListener {
            shareCurrentSong()
        }

        binding.backBtnPA.setOnClickListener {
            onBackPressed()
        }

        binding.repeatBtnPA.setOnClickListener {
            isRepeat = !isRepeat
            MusicService.mediaPlayer?.isLooping = isRepeat

            if (isRepeat) {
                binding.repeatBtnPA.setImageResource(R.drawable.repeat_one_icon)
                Toast.makeText(this, "Current Song", Toast.LENGTH_SHORT).show()
            } else {
                binding.repeatBtnPA.setImageResource(R.drawable.repeat_icon)
                Toast.makeText(this, "Loop All", Toast.LENGTH_SHORT).show()
            }

            saveRepeatState()
        }

        loadRepeatState()
        favAudioSet()

    }


    private fun updateSeekBar() {

        handler = Handler(Looper.getMainLooper())

        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                MusicService.mediaPlayer?.let {
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

    }


    private fun playingSeekBar() {

        val mp = MusicService.mediaPlayer
        if (!isUserSeeking && mp?.isPlaying == true) {
            seekBar.progress = mp.currentPosition
            startTime.text = formatTime(mp.currentPosition)
            seekBar.max = mp.duration
            endTime.text = formatTime(mp.duration)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // just show the time while scrubbing; do NOT call seek here (optional)
                    startTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isUserSeeking = true
                startService(Intent(this@PlayerActivity, MusicService::class.java).apply {
                    action = Constants.ACTION_SEEK_TO
                    putExtra("seekToMs", seekBar.progress)
                })
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isUserSeeking = false
                // Ask the service to seek
                startService(Intent(this@PlayerActivity, MusicService::class.java).apply {
                    action = Constants.ACTION_SEEK_TO
                    putExtra("seekToMs", seekBar.progress)
                })
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
            val song = musicListPA[songPosition]
            binding.songNamePA.text = song.title
            binding.songAlbumPA.text = song.album


            val isFav = FavoriteManager.isFavorite(song)
            binding.favouriteBtnPA.setImageResource(
                if (isFav) R.drawable.favourite_filled_icon
                else R.drawable.favourite_empty_icon
            )

            Glide.with(this@PlayerActivity.applicationContext)
                .load(song.artUri)
                .apply(RequestOptions().placeholder(R.drawable.itunes).centerCrop())
                .into(binding.songImgPA)

//            binding.startTimePA.text = formatTime(MusicService.mediaPlayer!!.currentPosition)
            binding.endTimePA.text = formatTime(MusicService.mediaPlayer!!.duration)
            binding.seekBarPA.progress = MusicService.mediaPlayer!!.currentPosition
            binding.seekBarPA.max = MusicService.mediaPlayer!!.duration

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playAudio() {
        MusicService.mediaPlayer?.start()
        isPlaying = true
        binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
    }

    private fun pauseAudio() {
        MusicService.mediaPlayer?.pause()
        isPlaying = false
        binding.playPauseBtnPA.setImageResource(R.drawable.play_icon)
    }

    private fun initializeLayout() {
        val incomingPosition = intent.getIntExtra("index", 0)
        val incomingSource = intent.getStringExtra("class")

        when (incomingSource) {

            "MainActivity" -> {
                musicListPA.clear()
                musicListPA.addAll(MainActivity.musicListMA)
                musicListPA.shuffle()
            }

            "MusicAdapter" -> {
                if (musicListPA.isEmpty()) {
                    musicListPA.addAll(MainActivity.musicListMA)
                }
            }

            "FavoriteActivity" -> {
                musicListPA.clear()
                musicListPA.addAll(FavoriteActivity.allSongs)
            }

            "Notification" -> {
                songPosition = MusicService.position

                // Ensure musicListPA is not empty (in case activity was killed and recreated)
                if (musicListPA.isEmpty()) {
                    musicListPA.addAll(MainActivity.musicListMA)
                }
            }

            "MiniPlayer" -> {
                // If sender passed playlist, use it; otherwise fallback to service playlist safely
                val receivedList = intent.getSerializableExtra("musicList") as? ArrayList<Music>
                if (receivedList != null && receivedList.isNotEmpty()) {
                    musicListPA.clear()
                    musicListPA.addAll(receivedList)
                } else {
                    // fallback: if service has playlist, copy that
                    if (MusicService.playlist.isNotEmpty()) {
                        musicListPA.clear()
                        musicListPA.addAll(MusicService.playlist)
                    }
                }
                songPosition = incomingPosition
            }

//            "MiniPlayer" -> {
//                musicListPA.clear()
//                songPosition = incomingPosition
//
//                if (musicListPA.isEmpty()) {
//                    musicListPA.addAll(MainActivity.musicListMA)
//                }
//                setLayout()
//               // ✔ correct current position
//            }


        }

        if (musicListPA.isEmpty()) {
            Toast.makeText(this, "No songs found to play", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (incomingPosition != songPosition || MusicService.mediaPlayer == null) {
            songPosition = incomingPosition
            setLayout()
            //createdMusicService.mediaPlayer()

        } else {
            setLayout() // update UI with current song info
            if (MusicService.mediaPlayer?.isPlaying == true) {
                binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
            } else {
                binding.playPauseBtnPA.setImageResource(R.drawable.play_icon)
            }
        }
    }


    fun nextPrevSong(increment: Boolean) {
        if (increment) {
            setSongPosition(true)
            setLayout()
            Log.d("sainiplayeractivity", "songplus")
        } else {
            setSongPosition(false)
            setLayout()
            Log.d("sainiplayeractivity", "songminus")
        }
    }

    private fun setSongPosition(increment: Boolean) {
        if (increment) {
            if (musicListPA.size - 1 == songPosition)
                songPosition = 0
            else songPosition++
        } else {
            if (songPosition == 0)
                songPosition = musicListPA.size - 1
            else songPosition--
        }
    }


    override fun onResume() {
        super.onResume()

//       updateFavoriteIcon()
        updateFavIcon()

        val filter = IntentFilter().apply {
            addAction("UPDATE_UI")
            addAction("MUSIC_PLAYER_UI_UPDATE")
        }
        registerReceiver(updateReceiver, filter)
        sendBroadcast(Intent("REQUEST_UI_UPDATE"))
        updatePlayPauseIcon()

    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(updateReceiver)
    }

    private val songControlReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_NEXT_SONG" -> nextPrevSong(true)
                "ACTION_PREV_SONG" -> nextPrevSong(false)
            }
        }
    }

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val idx = intent?.getIntExtra("Index", songPosition) ?: songPosition
            songPosition = idx.coerceAtLeast(0)

            runOnUiThread {
                setLayout()
                playingSeekBar()
                updatePlayPauseIcon()
            }
        }
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()

        val filter = IntentFilter().apply {
            addAction("UPDATE_UI")
            addAction("MUSIC_PLAYER_UI_UPDATE")
        }
        registerReceiver(updateReceiver, filter)


    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(updateReceiver)
            //   unregisterReceiver(uiUpdateReceiver)
        } catch (_: Exception) {
        }
    }

    private fun repeatSong() {

        binding.repeatBtnPA.setOnClickListener {
            isRepeat = !isRepeat
            MusicService.mediaPlayer?.isLooping = isRepeat

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
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this audio:\nTitle: ${song.title}\nArtist: ${song.artist}"
                )
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
        MusicService.mediaPlayer?.isLooping = isRepeat
    }

    private fun saveRepeatState() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        prefs.edit().putBoolean("isRepeat", isRepeat).apply()
    }


    private fun updatePlayPauseIcon() {
        if (MusicService.mediaPlayer?.isPlaying == true) {
            binding.playPauseBtnPA.setImageResource(R.drawable.pause_icon)
        } else {
            binding.playPauseBtnPA.setImageResource(R.drawable.play_icon)
        }
    }

    private fun favAudioSet() {
//        binding.favouriteBtnPA.setOnClickListener {
//
//            val currentSong = musicListPA[songPosition]
//            val favSongAdded = FavoriteManager.toggleFavorite(currentSong)
//
//            if (favSongAdded) {
//                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_filled_icon)
//                Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
//            } else {
//                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
//                Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show()
//            }
//        }

        binding.favouriteBtnPA.setOnClickListener {
            val currentSong = musicListPA[songPosition]

            favViewModel.toggle(currentSong.id)
            updateFavIcon()
        }

    }

    private fun playerList() {
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

//    private fun updateFavoriteIcon() {
//        val currentSong = musicListPA[songPosition]
//        val isFav = FavoriteManager.isFavorite(currentSong)
//        binding.favouriteBtnPA.setImageResource(
//            if (isFav) R.drawable.favourite_filled_icon
//            else R.drawable.favourite_empty_icon
//        )
//    }

    private fun updateFavIcon() {
        val currentSong = musicListPA[songPosition]

        lifecycleScope.launch {
            val isFav = favViewModel.isFavorite(currentSong.id)
            binding.favouriteBtnPA.setImageResource(
                if (isFav) R.drawable.favourite_filled_icon
                else R.drawable.favourite_empty_icon
            )
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


