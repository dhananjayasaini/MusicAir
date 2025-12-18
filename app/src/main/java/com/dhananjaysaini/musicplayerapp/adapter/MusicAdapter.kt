package com.dhananjaysaini.musicplayerapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.databinding.MusicViewBinding
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.modal.formatDuration
import java.io.Serializable

class MusicAdapter(
    private val context: Context,
    private var musicList: ArrayList<Music>,
    private val adapterClass: String
)
    : RecyclerView.Adapter<MusicAdapter.MyHolder>() {

    // Stores favorite song IDs
    private val favoriteIds = mutableSetOf<String>()

    var onItemClick: ((Int) -> Unit)? = null


    // Stores playlists: playlist name -> song IDs
    private val playlists = mutableMapOf<String, MutableList<String>>()

    class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val duration = binding.songDuration
        val image = binding.imageMV
        val menuIcon = binding.menuIcon
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)


        holder.root.setOnClickListener{

//            val serviceIntent = Intent(context, MusicService::class.java)
//            serviceIntent.action = Constants.ACTION_PLAY_NEW_LIST
//            serviceIntent.putExtra("musicList", ArrayList(MainActivity.musicListMA))
//            serviceIntent.putExtra("songPosition", position)
//            ContextCompat.startForegroundService(context, serviceIntent)

            val playerIntent = Intent(context, PlayerActivity::class.java)
            playerIntent.putExtra("index", position)
            playerIntent.putExtra("class", adapterClass)
            playerIntent.putExtra("musicList", musicList as Serializable)
            ContextCompat.startActivity(context, playerIntent, null)
        }

        val song = musicList[position]

        holder.menuIcon.setOnClickListener{
            showPopupMenu(it, song, position)
        }
//
//        holder.itemView.setOnClickListener {
//            onItemClick?.invoke(position)
//        }

        Glide.with(context)
            .load(musicList[position].artUri)
            .apply (RequestOptions().placeholder(R.drawable.itunes).centerCrop())
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    /** Toggle favorite state */
    fun toggleFavorite(song: Music) {
        if (favoriteIds.contains(song.id)) favoriteIds.remove(song.id)
        else favoriteIds.add(song.id)
        notifyDataSetChanged()
    }

    /** Get favorite songs */
    fun getFavoriteSongs(list: ArrayList<Music>): List<Music> = musicList.filter { favoriteIds.contains(it.id) }

//    fun getFavoriteSongs(list: ArrayList<Music>) {
//        musicList = list
//        notifyDataSetChanged()
//    }

    /** Add song to a playlist */
    fun addToPlaylist(playlistName: String, song: Music) {
        val list = playlists.getOrPut(playlistName) { mutableListOf() }
        if (!list.contains(song.id)) list.add(song.id)
    }

    /** Get songs from a playlist */
    fun getPlaylistSongs(playlistName: String): List<Music> {
        val ids = playlists[playlistName] ?: return emptyList()
        return musicList.filter { ids.contains(it.id) }
    }

    private fun showPopupMenu(view: View, song: Music, position: Int) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.music_item_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add_to_playlist -> {
                    Toast.makeText(view.context, "${song.title} Added", Toast.LENGTH_SHORT).show()

                    true
                }

                R.id.delete -> {
                    true
                }

                else -> true
            }

            }

        popup.show()
    }


}