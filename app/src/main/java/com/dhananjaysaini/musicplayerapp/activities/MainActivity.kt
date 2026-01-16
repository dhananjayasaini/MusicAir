package com.dhananjaysaini.musicplayerapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.adapter.ViewPagerAdapter
import com.dhananjaysaini.musicplayerapp.databinding.ActivityMainBinding
import com.dhananjaysaini.musicplayerapp.fragments.MiniPlayerFragment
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.utils.FavouriteManager
import com.dhananjaysaini.musicplayerapp.utils.VoiceControlManager
import com.dhananjaysaini.musicplayerapp.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.system.exitProcess
/*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var voiceControl: VoiceControlManager
    private lateinit var mainAdapter: MainAdapter

    companion object {
        var musicListMA = ArrayList<Music>()
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FavouriteManager.init(applicationContext)

        setupUI()
        observeViewModel()
        requestPermission()
        checkAudioPermission()
        voiceControllerMa()

//        binding.viewPager.adapter = FeaturePagerAdapter(this)
//
//        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
//            tab.text = when (position) {
//                0 -> "Home"
//                1 -> "All Songs"
//                2 -> "Favourite"
//                3 -> "Playlists"
//                else -> "Home"
//            }
//        }.attach()
//


    }


    private fun observeViewModel() {

        viewModel.musicListLiveData.observe(this, Observer { list ->
            musicListMA = list
            setupRecyclerView()
            binding.totalSongs.text = "${list.size} Songs"
            //startServiceForNotification()
        })

        viewModel.error.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setupUI() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mini_player_container, MiniPlayerFragment())
            .commit()

        registerReceiver(showMiniReceiver, IntentFilter("SHOW_MINI_PLAYER"))

        binding.shuffleBtn.setOnClickListener {
            if (musicListMA.isNotEmpty()) {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("index", 0)
                intent.putExtra("class", "MainActivity")

                startActivity(intent)
            }

        }

        binding.favouriteBtn.setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            intent.putExtra("ALL_SONGS", ArrayList(musicListMA))
            startActivity(intent)
        }

        setupDrawerMenu()
    }

//    private fun setupRecyclerView() {
//        musicAdapter = MusicAdapter(this, musicListMA, "MusicAdapter")
//        binding.musicRV.apply {
//            adapter = musicAdapter
//            layoutManager = LinearLayoutManager(this@MainActivity)
//            setHasFixedSize(true)
//            setItemViewCacheSize(10)
//        }
//        musicAdapter.notifyDataSetChanged()
//    }

    private fun setupRecyclerView(){

        mainAdapter = MainAdapter(this, MainActivity.musicListMA)

        binding.musicRV.adapter = mainAdapter
        binding.musicRV.layoutManager = LinearLayoutManager(this)

        mainAdapter.onItemClick = { list, position ->
            handleSongClick(list, position)
        }
    }

    private fun handleSongClick(list: ArrayList<Music>, position: Int) {

        MusicService.playlist = ArrayList(list)
        MusicService.position = position

        startService(Intent(this, MusicService::class.java).apply {
            action = Constants.ACTION_PLAY_AT
            putExtra("songPosition", position)

        }
        )
            startActivity(Intent(this, PlayerActivity::class.java))


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item))
            true
        else super.onOptionsItemSelected(item)
    }

    private fun setupDrawerMenu() {
        val drawer = binding.drawerBtn
        toggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.navSetting -> Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show()
                R.id.navAbout -> Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                R.id.navExit -> exitProcess(1)
            }
            true
        }
    }

    private fun requestPermission() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (!hasPermission(Manifest.permission.READ_MEDIA_AUDIO)) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }

            // Notification permission Android 13+
            if (!hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        else {
            // Android 12 or lower
            if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                500 // single request code
            )
        } else {
            viewModel.loadMusic()
        }
    }

    private fun hasPermission(perm: String) =
        ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if((grantResults.isNotEmpty() && grantResults[0] ==  PackageManager.PERMISSION_GRANTED ))

        if (requestCode == 500) {

//            val allGranted = grantResults.isNotEmpty() &&
//                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }
//            if (allGranted) {

            if((grantResults.isNotEmpty() && grantResults[0] ==  PackageManager.PERMISSION_GRANTED )){

            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                viewModel.loadMusic()

            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startServiceForNotification() {
        if (musicListMA.isEmpty()) return
        val song = musicListMA[0]

        val serviceIntent = Intent(this, MusicService::class.java).apply {
            putExtra("SONG_ID", song.id)
            putExtra("SONG_TITLE", song.title)
            putExtra("SONG_ALBUM", song.album)
            putExtra("SONG_ARTIST", song.artist)
            putExtra("SONG_DURATION", song.duration)
            putExtra("SONG_PATH", song.path)
            putExtra("SONG_ART_URI", song.artUri)
        }

        ContextCompat.startForegroundService(this, serviceIntent)

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

    private val showMiniReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            binding.miniPlayerContainer.visibility = View.VISIBLE
        }
    }

    private fun voiceControllerMa() {
        voiceControl = VoiceControlManager(this)
        binding.voiceControlMa.btnMic.setOnClickListener {
            voiceControl.startListening()
        }
    }

    private fun checkAudioPermission() {
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 101)
        }

    }



}
*/

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var voiceControl: VoiceControlManager
     val mainViewModel: MainViewModel by viewModels()

    companion object {
        var musicListMA = ArrayList<Music>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FavouriteManager.init(applicationContext)

        setupViewPager()
        setupDrawerMenu()
        setupMiniPlayer()
        observeViewModel()
        requestPermission()
        checkAudioPermission()
        //voiceControllerMa()
        mainSongsVM()

    }

    // -------------------- ViewPager --------------------

    private fun setupViewPager() {
        binding.viewPager.adapter = ViewPagerAdapter(this)
        binding.viewPager.offscreenPageLimit = 5

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Home"
                1 -> "Songs"
                2 -> "Favourite"
                3 -> "Playlists"
                4 -> "Folder"
                else -> "Home"
            }
        }.attach()
    }

    // -------------------- ViewModel --------------------

    private fun observeViewModel() {
        mainViewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    // -------------------- Mini Player --------------------

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setupMiniPlayer() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mini_player_container, MiniPlayerFragment())
            .commit()

        registerReceiver(
            showMiniReceiver,
            IntentFilter("SHOW_MINI_PLAYER")
        )
    }

    private val showMiniReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            binding.miniPlayerContainer.visibility = View.VISIBLE
        }
    }

    // -------------------- Drawer --------------------

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun setupDrawerMenu() {
        val drawer = binding.drawerBtn
        toggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> toast("Feedback")
                R.id.navSetting -> toast("Setting")
                R.id.navAbout -> toast("About")
                R.id.navExit -> exitProcess(1)
            }
            true
        }
    }

    // -------------------- Permissions --------------------

    private fun requestPermission() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasPermission(Manifest.permission.READ_MEDIA_AUDIO))
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)

            if (!hasPermission(Manifest.permission.POST_NOTIFICATIONS))
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 500)
        } else {
            mainViewModel.loadMusic()
        }
    }

    private fun hasPermission(perm: String) =
        ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 500 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            mainViewModel.loadMusic()
        } else {
            toast("Permission Denied")
        }
    }

    private fun checkAudioPermission() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 101)
        }
    }

    // -------------------- Voice --------------------

    private fun voiceControllerMa() {
        voiceControl = VoiceControlManager(this)
//        binding.voiceControlMa.btnMic.setOnClickListener {
//            voiceControl.startListening()
//        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

     fun mainSongsVM(){
        val mainViewModel = ViewModelProvider(this)
            .get(MainViewModel::class.java)

        mainViewModel.loadMusic()

    }
}
