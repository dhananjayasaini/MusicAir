package com.dhananjaysaini.musicplayerapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlaylistSongsActivity
import com.dhananjaysaini.musicplayerapp.adapter.PlaylistAdapter
import com.dhananjaysaini.musicplayerapp.databinding.FragmentPlaylistBinding
import com.dhananjaysaini.musicplayerapp.model.PlaylistEntity
import com.dhananjaysaini.musicplayerapp.viewmodel.PlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistFragment : Fragment(R.layout.fragment_playlist) {

    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var playlistViewModel: PlaylistViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlaylistBinding.bind(view)

        playlistViewModel = ViewModelProvider(requireActivity())
            .get(PlaylistViewModel::class.java)

        playlistAdapter = PlaylistAdapter(arrayListOf())

        binding.playlistRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistRecycler.adapter = playlistAdapter

        playlistViewModel.playlists.observe(viewLifecycleOwner) {

            val ordered = it.sortedByDescending { it.createdAt }

            playlistAdapter.update(ordered)

            binding.totalPlaylist.text = when (it.size) {
                0 -> "0 Playlist"
                1 -> "1 Playlist"
                else ->  "${it.size} Playlists"
            }
        }

        binding.btnCreatePlaylist.setOnClickListener {
            showCreateDialog()
        }

        playlistAdapter.onRenameClick = { playlist ->
            showRenameDialog(playlist)
        }

        openPlaylistSongs()
        deletePlaylist()
    }

    private fun openPlaylistSongs() {

        playlistAdapter.onItemClick = { playlist ->
            val intent = Intent(requireContext(), PlaylistSongsActivity::class.java)
            intent.putExtra("playlistId", playlist.songId)
            intent.putExtra("playlistName", playlist.name)

            startActivity(intent)
        }
    }

    private fun showCreateDialog() {
        val editText = EditText(requireContext())

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create Playlist")
            .setView(editText)
            .setPositiveButton("Create") { _, _ ->
                playlistViewModel.createPlaylist(editText.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(Color.BLACK)

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(Color.BLACK)
    }

    private fun deletePlaylist(){
        playlistAdapter.onDeleteClick = { playlist ->

            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete playlist")
                .setMessage("Delete '${playlist.name}'?")
                .setPositiveButton("Delete") { _, _ ->
                    playlistViewModel.deletePlaylist(playlist.songId)
                }
                .setNegativeButton("Cancel", null)
                .show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(Color.BLACK)

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(Color.BLACK)
        }
    }

    private fun showRenameDialog(playlist: PlaylistEntity) {

        val editText = EditText(requireContext()).apply {
            setText(playlist.name)
            setSelection(text.length)
        }

       val dialog =  MaterialAlertDialogBuilder(requireContext())
            .setTitle("Rename playlist")
            .setView(editText)
            .setPositiveButton("Rename") { _, _ ->

                val editName = editText.text.toString().trim()

                if (editName.isNotEmpty() && editName != playlist.name) {
                    playlistViewModel.renamePlaylist(
                        playlist.songId,
                        editName
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(Color.BLACK)

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(Color.BLACK)
    }


}


