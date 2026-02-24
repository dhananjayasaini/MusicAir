package com.dhananjaysaini.musicplayerapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.adapter.ViewPagerAdapter
import com.dhananjaysaini.musicplayerapp.databinding.ActivityMainBinding
import com.dhananjaysaini.musicplayerapp.fragments.MiniPlayerFragment
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.utils.FavouriteManager
import com.dhananjaysaini.musicplayerapp.utils.VoiceControlManager
import com.dhananjaysaini.musicplayerapp.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var voiceControl: VoiceControlManager
     private val mainViewModel: MainViewModel by viewModels()

    companion object {
        var musicListMA = ArrayList<Music>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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
        binding.viewPager.offscreenPageLimit = 7

        binding.viewPager.setCurrentItem(1, false)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Home"
                1 -> "Songs"
                2 -> "Favourite"
                3 -> "Playlists"
                4 -> "Artist"
                5 -> "Albums"
                6 -> "Folder"
                else -> "Songs"
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
                R.id.navFeedback -> sendFeedback(this)
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

//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == 500) {
//
//            var audioPermissionGranted = false
//
//            for (i in permissions.indices) {
//                if (
//                    permissions[i] == Manifest.permission.READ_MEDIA_AUDIO ||
//                    permissions[i] == Manifest.permission.READ_EXTERNAL_STORAGE
//                ) {
//                    audioPermissionGranted =
//                        grantResults[i] == PackageManager.PERMISSION_GRANTED
//                }
//            }
//
//            if (audioPermissionGranted) {
//                mainViewModel.loadMusic()
//            } else {
//                toast("Storage Permission Denied")
//            }
//        }
//    }

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

     private fun mainSongsVM(){

         mainViewModel.musicListLiveData.observe(this) { songs ->
             MusicService.allSongs = songs
         }


     }

    private fun sendFeedback(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps will open
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@musicplayer.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Feedback - Music Player App")
            putExtra(Intent.EXTRA_TEXT,
                "Write your feedback here...\n\n" +
                        "---- Device Info ----\n" +
                        "Device: ${Build.MANUFACTURER} ${Build.MODEL}\n" +
                        "Android Version: ${Build.VERSION.RELEASE}\n" +
                        "App Version: ${getAppVersion(context)}\n"
            )
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send Feedback"))
        } catch (e: Exception) {
            Toast.makeText(context, "No email app found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val versionInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            versionInfo.versionName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }


}
