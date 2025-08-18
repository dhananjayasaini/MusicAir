package com.dhananjaysaini.musicplayerapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.databinding.MusicViewBinding
import com.dhananjaysaini.musicplayerapp.modal.Music
import com.dhananjaysaini.musicplayerapp.modal.formatDuration

class MusicAdapter(
    private val context: Context,
    private val musicList: ArrayList<Music>,
    private val onAddToPlaylist: (Music) -> Unit,
    private val adapterClass: String
)
    : RecyclerView.Adapter<MusicAdapter.MyHolder>() {

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
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", adapterClass)
            ContextCompat.startActivity(context, intent, null)
        }

        val song = musicList[position]

        holder.menuIcon.setOnClickListener{
            showPopupMenu(it, song, position)
        }

        Glide.with(context)
            .load(musicList[position].artUri)
            .apply (RequestOptions().placeholder(R.drawable.itunes).centerCrop())
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(newList: List<Music>) {
        musicList.clear()
        musicList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun showPopupMenu(view: View, song: Music, position: Int) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.music_item_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add_to_playlist -> {
                    Toast.makeText(view.context, "${song.title} Added", Toast.LENGTH_SHORT).show()

                    onAddToPlaylist(song)
                    true
                }

                R.id.delete -> {
                  //  onDeleteSong(song, position)
                    true
                }


//                R.id.delete -> {
//                    val uri = ContentUris.withAppendedId(
//                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                        song.id.toLong()
//                    )
//                    val contentResolver = context.contentResolver
//
//                    try {
//                        val rowsDeleted = contentResolver.delete(uri, null, null)
//                        if (rowsDeleted > 0) {
//                            musicList.removeAt(position)
//                            notifyItemRemoved(position)
//                            Toast.makeText(
//                                view.context,
//                                "${song.title} Deleted",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            Toast.makeText(
//                                view.context,
//                                "Failed to delete ${song.title}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    } catch (e: SecurityException) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
//                            val intentSender = e.userAction.actionIntent.intentSender
//                            val request = IntentSenderRequest.Builder(intentSender).build()
//
//                            // Tell the activity to remember what to delete
//                            if (context is MainActivity) {
//                                context.pendingDeleteSong = song
//                                context.pendingDeletePosition = position
//                                context.deletePermissionLauncher.launch(request)
//                            }
//                           // deletePermissionLauncher.launch(request)
//                        } else {
//                            Toast.makeText(
//                                view.context,
//                                "Cannot delete ${song.title}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//
//                    true
//                }

                else -> true
            }

            }

        popup.show()
    }


}