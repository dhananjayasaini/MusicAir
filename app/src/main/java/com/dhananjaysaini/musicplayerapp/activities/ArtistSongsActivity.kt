package com.dhananjaysaini.musicplayerapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.fragments.ArtistSongsFragment

class ArtistSongsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_songs)

        if (savedInstanceState == null) {

            val fragment = ArtistSongsFragment().apply {
                arguments = intent.extras
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.artistSongsContainer, fragment)
                .commit()
        }
    }
}
