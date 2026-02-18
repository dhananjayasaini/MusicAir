package com.dhananjaysaini.musicplayerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.model.MusicArtist

class AlbumAdapter(
    private val list: MutableList<MusicArtist>
) : RecyclerView.Adapter<AlbumAdapter.Holder>() {

    var onItemClick: ((MusicArtist) -> Unit)? = null

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.itemName)
        val subName: TextView = view.findViewById(R.id.itemSubName)
        val count: TextView = view.findViewById(R.id.itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_grid, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val album = list[position]

        holder.name.text = album.name
        holder.subName.text = album.name

        holder.count.text =
            if (album.songCount == 1)
                "1 song"
            else
                "${album.songCount} songs"

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(album)
        }
    }

    override fun getItemCount() = list.size

    fun update(newList: List<MusicArtist>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}

