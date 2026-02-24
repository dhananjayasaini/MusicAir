package com.dhananjaysaini.musicplayerapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.adapter.AlbumSongsAdapter
import com.dhananjaysaini.musicplayerapp.adapter.ArtistSongsAdapter
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.databinding.FragmentAlbumBinding
import com.dhananjaysaini.musicplayerapp.databinding.FragmentAlbumSongsBinding
import com.dhananjaysaini.musicplayerapp.databinding.FragmentArtistSongsBinding
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService

class AlbumSongsFragment : Fragment(R.layout.fragment_album_songs) {

    private lateinit var binding: FragmentAlbumSongsBinding
    private lateinit var albumSongsAdapter: AlbumSongsAdapter
    private val songs = ArrayList<Music>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAlbumSongsBinding.bind(view)

        val albumName = requireArguments().getString("albumName") ?: return

        binding.albumTitle.text = albumName

        songs.addAll(
            MusicService.allSongs.filter {
                it.album == albumName
            }
        )

        albumSongsAdapter = AlbumSongsAdapter(requireContext(), songs)

        val recycler = binding.albumSongsRecycler

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = albumSongsAdapter

        albumSongsAdapter.onItemClick = { list, position ->

            MusicService.playlist = list
            MusicService.position = position

            requireContext().startService(
                Intent(requireContext(), MusicService::class.java)
                    .setAction(Constants.ACTION_PLAY_AT)
                    .putExtra("songPosition", position)
            )
            startActivity(Intent(requireContext(), PlayerActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }

        updateSongs(albumName)
    }

    private fun updateSongs(album: String) {

        val allSongs = MusicService.allSongs

        songs.clear()
        songs.addAll(
            allSongs.filter { it.album == album }
        )

        albumSongsAdapter.updateList(ArrayList(songs))

        binding.albumSongCount.text = when (songs.size) {
            0 -> "No Song"
            1 -> "1 Song"
            else -> "${songs.size} Songs"
        }
    }
}
