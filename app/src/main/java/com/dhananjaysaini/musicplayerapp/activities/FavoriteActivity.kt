package com.dhananjaysaini.musicplayerapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.adapter.MusicAdapter
import com.dhananjaysaini.musicplayerapp.databinding.ActivityFavoriteBinding
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.utils.FavoriteManager

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
}