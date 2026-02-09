package com.dhananjaysaini.musicplayerapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.fragments.FolderSongsFragment

class FolderSongsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_songs)

        if (savedInstanceState == null) {

            val fragment = FolderSongsFragment().apply {
                arguments = intent.extras
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.folderSongsContainer, fragment)
                .commit()
        }
    }
}