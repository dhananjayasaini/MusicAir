package com.dhananjaysaini.musicplayerapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.fragments.PlaylistSongsFragment

class PlaylistSongsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_songs)

        if (savedInstanceState == null) {

            val fragment = PlaylistSongsFragment().apply {
                arguments = intent.extras
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.playlistSongsContainer, fragment)
                .commit()
        }
    }
}
