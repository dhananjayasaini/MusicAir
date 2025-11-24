package com.dhananjaysaini.musicplayerapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.adapter.MusicAdapter
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.databinding.ActivityMainBinding
import com.dhananjaysaini.musicplayerapp.fragments.MiniPlayerFragment
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.utils.FavoriteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter


    companion object {
       var musicListMA : ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeLayout()
        requestRunTimePermission()

        FavoriteManager.init(applicationContext)


            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mini_player_container, MiniPlayerFragment())
                    .commit()
            }


        binding.favoriteBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
            intent.putExtra("ALL_SONGS", ArrayList(musicListMA)) // must be Serializable or Parcelable

            startActivity(intent)

        }

    }

// to start the navigation drawer

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item))
            true
        else super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "SuspiciousIndentation")
    private fun initializeLayout() {

        binding.shuffleBtn.setOnClickListener {
            if (musicListMA.size > 0) {
                val shuffledList = ArrayList(musicListMA.shuffled())
                val intent = Intent(this@MainActivity, PlayerActivity::class.java)
                intent.putExtra("index", 0)
                intent.putExtra("class", "MainActivity")
                startActivity(intent)
            } else {
                Toast.makeText(this, "No songs available to play", Toast.LENGTH_SHORT).show()
            }
        }



        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> Toast.makeText(baseContext, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.navSetting -> Toast.makeText(baseContext, "Setting", Toast.LENGTH_SHORT).show()
                R.id.navAbout -> Toast.makeText(baseContext, "About", Toast.LENGTH_SHORT).show()
                R.id.navExit -> exitProcess(1)
            }
            true
        }

        val drawerLayout = binding.drawerBtn
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.onDrawerOpened(drawerLayout)
        toggle.onDrawerClosed(drawerLayout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CoroutineScope(Dispatchers.IO).launch {
            musicListMA = getAllAudio()
            withContext(Dispatchers.Main) {
                if (musicListMA.isNotEmpty()) {

                    musicAdapter = MusicAdapter(
                        this@MainActivity,
                        musicListMA,
                        adapterClass = "MusicAdapter",
                    )

                    binding.musicRV.adapter = musicAdapter
                    musicAdapter.notifyDataSetChanged()
                    binding.totalSongs.text = musicAdapter.itemCount.toString() + " Songs"

                    notificationBuilder()
                }
                else {
                    Toast.makeText(this@MainActivity, "No songs found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Log.d("AudioTag", "initialSize: "+ musicListMA.size)

        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(10)
        binding.musicRV.layoutManager = LinearLayoutManager(this)

    }

    @SuppressLint("Recycle", "Range", "SuspiciousIndentation")
    private fun getAllAudio() : ArrayList<Music>{

        val audioList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 "
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        val cursor = this.contentResolver.query(uri, projection, selection, null , sortOrder)
        if (cursor!=null) {
            if (cursor.moveToFirst())
                do {
                    val titleC= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC= cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC= cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = Music(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC,
                        duration = durationC, artUri = artUriC)
                    val file = File(music.path)
                    Log.d("AudioTag", "getAllAudio: " + file.exists())

                    if (file.exists())
                        audioList.add(music)
                }
                while (cursor.moveToNext())
                cursor.close()
        }

        Log.d("AudioTag", "audioSize: "+ audioList.size)
        return audioList

    }

    private fun requestRunTimePermission() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
            {
                permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            }

            else if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 11)
        } else {
            initializeLayout()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==11){
            if((grantResults.isNotEmpty() && grantResults[0] ==  PackageManager.PERMISSION_GRANTED )) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializeLayout()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun notificationBuilder(){

        if (musicListMA.isEmpty()) {
            Toast.makeText(this, "No songs found for notification", Toast.LENGTH_SHORT).show()
            return
        }

        val position = 0
        val song = musicListMA[position]

        val serviceIntent = Intent(this, MusicService::class.java).apply {
            action = Constants.ACTION_PLAY
            action = Constants.ACTION_PAUSE
            action = Constants.ACTION_NEXT
            action = Constants.ACTION_PREVIOUS
            action = Constants.CHANNEL_ID
            action = Constants.NOTIFICATION_ID.toString()

            putExtra("SONG_ID", song.id)
            putExtra("SONG_TITLE", song.title)
            putExtra("SONG_ALBUM", song.album)
            putExtra("SONG_ARTIST", song.artist)
            putExtra("SONG_DURATION", song.duration)
            putExtra("SONG_PATH", song.path)
            putExtra("SONG_ART_URI", song.artUri)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            }
        ContextCompat.startForegroundService(this, serviceIntent) // safe for API 26+

        val playerIntent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("SONG_ID", song.id)
            putExtra("SONG_TITLE", song.title)
            putExtra("SONG_ALBUM", song.album)
            putExtra("SONG_ARTIST", song.artist)
            putExtra("SONG_DURATION", song.duration)
            putExtra("SONG_PATH", song.path)
            putExtra("SONG_ART_URI", song.artUri)
        }
        this.startActivity(playerIntent)

    }


}