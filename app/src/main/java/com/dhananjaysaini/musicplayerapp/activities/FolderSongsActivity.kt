package com.dhananjaysaini.musicplayerapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.fragments.FolderSongsFragment
import com.dhananjaysaini.musicplayerapp.utils.ThemeManager

class FolderSongsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_songs)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true  // dark icons

        if (savedInstanceState == null) {

            val fragment = FolderSongsFragment().apply {
                arguments = intent.extras
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.folderSongsContainer, fragment)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeManager.applyThemeToActivity(this)
    }
}