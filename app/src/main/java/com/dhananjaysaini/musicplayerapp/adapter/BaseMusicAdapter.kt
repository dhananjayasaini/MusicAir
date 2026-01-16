package com.dhananjaysaini.musicplayerapp.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.dhananjaysaini.musicplayerapp.databinding.MusicViewBinding
import com.dhananjaysaini.musicplayerapp.model.Music

abstract class BaseMusicAdapter(
    protected val context: Context,
    protected var musicList: ArrayList<Music>
) : RecyclerView.Adapter<BaseMusicAdapter.MyHolder>() {

    var onItemClick: ((ArrayList<Music>, Int) -> Unit)? = null

    class MyHolder(binding: MusicViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val duration = binding.songDuration
        val image = binding.imageMV
        val menuIcon = binding.menuIcon
        val root = binding.root
    }

    override fun getItemCount(): Int = musicList.size

    open fun updateList(list: ArrayList<Music>) {
        musicList = list
        notifyDataSetChanged()
    }

    protected fun bindClick(holder: MyHolder, position: Int) {
        holder.root.setOnClickListener {
            onItemClick?.invoke(musicList, position)
        }
    }
}
