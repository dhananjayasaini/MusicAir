package com.dhananjaysaini.musicplayerapp.adapter

import android.content.Context
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.dhananjaysaini.musicplayerapp.databinding.MusicViewBinding
import com.dhananjaysaini.musicplayerapp.model.Music

abstract class BaseMusicAdapter(
    protected val context: Context,
    protected var musicList: ArrayList<Music>
) : RecyclerView.Adapter<BaseMusicAdapter.MyHolder>() {

    var onItemClick: ((ArrayList<Music>, Int) -> Unit)? = null
    var onAddToPlaylist: ((Music) -> Unit)? = null
    var onRemoveFromPlaylist: ((Music) -> Unit)? = null


    class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
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

        // 🔥 POPUP MENU
        holder.menuIcon.setOnClickListener { view ->

            val popup = PopupMenu(context, view)

            popup.menu.add(0, 1, 0, "Add to playlist")
            popup.menu.add(0, 2, 1, "Remove from playlist")

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    1 -> {
                        onAddToPlaylist?.invoke(musicList[position])
                        true
                    }

                    2 -> {
                        onRemoveFromPlaylist?.invoke(musicList[position])
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }

    }


}
