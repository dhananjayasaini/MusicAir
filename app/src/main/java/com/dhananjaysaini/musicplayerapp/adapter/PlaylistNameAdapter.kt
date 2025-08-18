package com.dhananjaysaini.musicplayerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dhananjaysaini.musicplayerapp.R

class PlaylistNameAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<PlaylistNameAdapter.ViewHolder>() {

    private var playlists: List<String> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.playlistName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_name, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.name.text = playlist
        holder.itemView.setOnClickListener { onClick(playlist) }
    }

    fun submitList(list: List<String>) {
        playlists = list
        notifyDataSetChanged()
    }
}
