package com.dhananjaysaini.musicplayerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.adapter.ArtistSongsAdapter
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.databinding.FragmentArtistSongsBinding
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService

class ArtistSongsFragment : Fragment(R.layout.fragment_artist_songs) {

    private lateinit var binding: FragmentArtistSongsBinding
    private lateinit var artistSongsAdapter: ArtistSongsAdapter
    private val songs = ArrayList<Music>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentArtistSongsBinding.bind(view)

        val artistName = requireArguments().getString("artistName") ?: return

        binding.artistTitle.text = artistName

        songs.addAll(
            MusicService.allSongs.filter {
                it.artist == artistName
            }
        )

        artistSongsAdapter = ArtistSongsAdapter(requireContext(), songs)

        val recycler = binding.artistSongsRecycler

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = artistSongsAdapter

        artistSongsAdapter.onItemClick = { list, position ->

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

        updateSongs(artistName)
    }

    private fun updateSongs(artist: String) {

        val allSongs = MusicService.allSongs

        songs.clear()
        songs.addAll(
            allSongs.filter { it.artist == artist }
        )

        artistSongsAdapter.updateList(ArrayList(songs))

        binding.artistSongCount.text = when (songs.size) {
            0 -> "No Song"
            1 -> "1 Song"
            else -> "${songs.size} Songs"
        }
    }
}
