package com.dhananjaysaini.musicplayerapp.service

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.modal.Music

class MusicService : Service() {

    private lateinit var receiver: BroadcastReceiver
    private var currentMusic: Music? = null

    companion object {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerReceiver()

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val music = intent?.getSerializableExtra("MUSIC") as? Music
        currentMusic = music
//        Log.d("MUSIC_SERVICE", "onStartCommand called")
        showNotification()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)

    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for music playback"
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

        }
    }

    private fun showNotification(
        isPlaying: Boolean = false,

    ) {
        Log.d("MUSIC_SERVICE", "Showing notification")


      //   val song = currentMusic ?: return

        val playPauseIcon = if (isPlaying) R.drawable.pause_icon else R.drawable.play_icon
        val playPauseAction = if (isPlaying) Constants.ACTION_PAUSE else Constants.ACTION_PLAY

        val playerIntent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("class", "Notification")
            putExtra("index", PlayerActivity.songPosition)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, playerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = Intent(Constants.ACTION_PREVIOUS).let {
            PendingIntent.getBroadcast(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        val playPauseIntent = Intent(playPauseAction).let {
            PendingIntent.getBroadcast(this, 1, it, PendingIntent.FLAG_IMMUTABLE)
        }

        val nextIntent = Intent(Constants.ACTION_NEXT).let {
            PendingIntent.getBroadcast(this, 2, it, PendingIntent.FLAG_IMMUTABLE)
        }

        val artwork = BitmapFactory.decodeResource(resources, R.drawable.itunes)

        val notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle(currentMusic?.title)
            .setContentText(currentMusic?.artist)
            .setSmallIcon(R.drawable.music_player_icon_splash_screen)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setLargeIcon(artwork)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.previous_icon, "Previous", prevIntent)
            .addAction(playPauseIcon, "PlayPause", playPauseIntent)
            .addAction(R.drawable.next_icon, "Next", nextIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setOnlyAlertOnce(true)
            .setOngoing(isPlaying)
            .build()

        startForeground(Constants.NOTIFICATION_ID, notification)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.ACTION_PLAY -> {
                        mediaPlayer?.start()
                        showNotification()
                    }

                    Constants.ACTION_PAUSE -> {
                       mediaPlayer?.pause()
                        showNotification()
                    }

                    Constants.ACTION_NEXT -> {
                        sendBroadcast(Intent("ACTION_NEXT_SONG"))
                    }

                    Constants.ACTION_PREVIOUS -> {
                        sendBroadcast(Intent("ACTION_PREV_SONG"))
                    }
                }
                sendUIUpdateBroadcast("playPause")
            }
        }

        val filter = IntentFilter().apply {
            addAction(Constants.ACTION_PLAY)
            addAction(Constants.ACTION_PAUSE)
            addAction(Constants.ACTION_NEXT)
            addAction(Constants.ACTION_PREVIOUS)
        }
        registerReceiver(receiver, filter)
    }

    private fun sendUIUpdateBroadcast(action: String) {
        val intent = Intent("MUSIC_PLAYER_UI_UPDATE")
        intent.putExtra("action", action)
        sendBroadcast(intent)
    }



}
