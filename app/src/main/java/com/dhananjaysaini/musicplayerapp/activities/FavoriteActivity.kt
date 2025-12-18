package com.dhananjaysaini.musicplayerapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.adapter.MusicAdapter
import com.dhananjaysaini.musicplayerapp.databinding.ActivityFavoriteBinding
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.viewmodal.FavoriteViewModel

/*
class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var musicadapter: MusicAdapter
    private lateinit var recyclerView : RecyclerView

    companion object {
        lateinit var allSongs: ArrayList<Music> // Load your main songs list here
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.favRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        allSongs = intent.getSerializableExtra("ALL_SONGS") as ArrayList<Music>
        musicadapter = MusicAdapter(this, allSongs, "FavoritesAdapter")

    //  val favoriteSongs = musicadapter.getFavoriteSongs()
    //  recyclerView.adapter = MusicAdapter(this, ArrayList(favoriteSongs), "FavoriteAdapter")

        binding.backBtnFav.setOnClickListener{
            onBackPressed()
        }

        favSongs()

    }

    fun favSongs(){
        val favoriteSongs = ArrayList(FavoriteManager.getFavorites())
        recyclerView.adapter = MusicAdapter(this, favoriteSongs, "FavoriteAdapter")
    }
}*/

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: MusicAdapter
    private val favViewModel: FavoriteViewModel by viewModels()
    private val favoriteSongs = ArrayList<Music>()

    companion object {
        lateinit var allSongs: ArrayList<Music> // Load your main songs list here
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.favRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this, ArrayList(), this@FavoriteActivity.toString())
        binding.favRecyclerView.adapter = adapter


        adapter.onItemClick = { position ->

         //   if (favoriteSongs.isEmpty()) return@onItemClick

            // IMPORTANT: favorite list ko playlist banao
            MusicService.playlist = ArrayList(favoriteSongs)

            // clicked position
            MusicService.position = position

            // service ko play command
            val intent = Intent(this, MusicService::class.java)
            intent.putExtra("action", "play")

            startService(intent)
        }

        favViewModel.favoriteIds.observe(this) { favIds ->

            favoriteSongs.clear()
            favoriteSongs.addAll(
                MainActivity.musicListMA.filter { it.id in favIds }
            )

            adapter.getFavoriteSongs(ArrayList(favoriteSongs))
        }

        favViewModel.loadFavorites()

        binding.backBtnFav.setOnClickListener{
            onBackPressed()
        }

    }
}

