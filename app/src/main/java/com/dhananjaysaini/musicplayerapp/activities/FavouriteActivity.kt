package com.dhananjaysaini.musicplayerapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.adapter.MusicAdapter
import com.dhananjaysaini.musicplayerapp.databinding.ActivityFavouriteBinding
import com.dhananjaysaini.musicplayerapp.utils.FavoritesManager
import com.dhananjaysaini.musicplayerapp.utils.PlaylistManager

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: MusicAdapter

    companion object {
        val favList = MainActivity.musicListMA.filter {
            FavoritesManager.isFavorite(it.id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerApp)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (favList.isEmpty()) {
            Toast.makeText(this, "No favorite songs found", Toast.LENGTH_SHORT).show()
        }
        

        adapter = MusicAdapter(
            this, favList as ArrayList, onAddToPlaylist = { song ->
                PlaylistManager.addToPlaylist(this, "MyPlaylist", song)
                Toast.makeText(this, "${song.title} ", Toast.LENGTH_SHORT).show()
            },
            adapterClass = "FavouriteActivity"
        )
        binding.favRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favRecyclerView.adapter = adapter

        binding.backBtnFav.setOnClickListener {
            onBackPressed()
        }

    }


}
