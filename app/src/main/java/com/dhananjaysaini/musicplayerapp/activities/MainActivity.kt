package com.dhananjaysaini.musicplayerapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.adapter.MusicAdapter
import com.dhananjaysaini.musicplayerapp.databinding.ActivityMainBinding
import com.dhananjaysaini.musicplayerapp.modal.Music
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
       var MusicListMA : ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeLayout()
        requestRunTimePermission()
    }

    private fun requestRunTimePermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 11)
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
          val intent = Intent(this@MainActivity, PlayerActivity::class.java)

            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }

        binding.favoriteBtn.setOnClickListener {
            startActivity(Intent(this, FavouriteActivity::class.java))
        }

        binding.playlistBtn.setOnClickListener {
            startActivity(Intent(this, PlaylistActivity::class.java))
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

//navigation drawer
        val drawerLayout = binding.drawerBtn
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.onDrawerOpened(drawerLayout)
        toggle.onDrawerClosed(drawerLayout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CoroutineScope(Dispatchers.IO).launch {
            MusicListMA = getAllAudio()
            withContext(Dispatchers.Main){
                musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
                binding.musicRV.adapter = musicAdapter
                musicAdapter.notifyDataSetChanged()
                binding.totalSongs.text = "Total Songs : "+musicAdapter.itemCount

            }
        }

        Log.d("AudioTag", "initialSize: "+ MusicListMA.size)

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==11){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 11)
            }
        }
    }


}