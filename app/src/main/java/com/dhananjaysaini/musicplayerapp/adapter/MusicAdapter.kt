package com.dhananjaysaini.musicplayerapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.databinding.MusicViewBinding
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.modal.formatDuration

class MusicAdapter(private val context: Context, private val musicList: ArrayList<Music>)
    : RecyclerView.Adapter<MusicAdapter.MyHolder>() {

  /*  private lateinit var context: Context
      private lateinit var musicList:ArrayList<String> */

    class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val duration = binding.songDuration
        val image = binding.imageMV
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
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter")
            ContextCompat.startActivity(context, intent, null)
        }

        Glide.with(context)
            .load(musicList[position].artUri)
            .apply (RequestOptions().placeholder(R.drawable.itunes).centerCrop())
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

}