package com.dhananjaysaini.musicplayerapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.fragments.PlaylistSongsFragment
import com.dhananjaysaini.musicplayerapp.utils.ThemeManager

class PlaylistSongsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_songs)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true  // dark icons

        if (savedInstanceState == null) {

            val fragment = PlaylistSongsFragment().apply {
                arguments = intent.extras
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.playlistSongsContainer, fragment) 
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeManager.applyThemeToActivity(this)
    }
}
