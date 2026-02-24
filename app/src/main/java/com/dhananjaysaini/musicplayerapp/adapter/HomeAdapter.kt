package com.dhananjaysaini.musicplayerapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.model.Music


class HomeAdapter(
    private val context: Context,
    private val list: ArrayList<Music>
) : RecyclerView.Adapter<HomeAdapter.Holder>() {

    var onItemClick: ((ArrayList<Music>, Int) -> Unit)? = null

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val homeSongTitle: TextView = view.findViewById(R.id.homeSongTitle)
        val homeSongImage: ImageView = itemView.findViewById(R.id.homeSongImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_song, parent, false)
        )
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val song = list[position]

        holder.homeSongTitle.text = song.title

        Glide.with(holder.itemView.context)
            .load(song.artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_folder).centerCrop())
            .into(holder.homeSongImage)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(list, position)
        }
    }

    override fun getItemCount() = list.size

    fun updateList(newList: ArrayList<Music>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}



