package com.dhananjaysaini.musicplayerapp.activities

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.adapter.MusicAdapter
import com.dhananjaysaini.musicplayerapp.databinding.ActivityPlaylistSongsBinding
import com.dhananjaysaini.musicplayerapp.utils.DeleteHandler
import com.dhananjaysaini.musicplayerapp.utils.PlaylistManager

class PlaylistSongsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistSongsBinding
    private lateinit var adapter: MusicAdapter
    private lateinit var playlistName: String
    private lateinit var deleteHandler: DeleteHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistSongsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playlistName = intent.getStringExtra("playlist_name") ?: return finish()

        val deleteLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            deleteHandler.onActivityResult(result.resultCode)
        }

        deleteHandler = DeleteHandler(
            this,
            applicationContext,
            deleteLauncher
        ) { position ->
            val list = PlaylistManager.getPlaylist(this, playlistName).toMutableList()
            list.removeAt(position)
            PlaylistManager.savePlaylist(this, playlistName, list)
            adapter.updateMusicList(list)
        }


        adapter = MusicAdapter(
            this,
            arrayListOf(),
            onAddToPlaylist = { song ->
                PlaylistManager.addToPlaylist(this, playlistName, song)
            },
            adapterClass = "PlaylistSongsActivity"
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        loadSongs()
    }


    private fun loadSongs() {
        val songs = PlaylistManager.getPlaylist(this, playlistName)
        adapter.updateMusicList(songs)
    }
}
