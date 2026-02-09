package com.dhananjaysaini.musicplayerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.adapter.PlaylistSongsAdapter
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.databinding.FragmentPlaylistBinding
import com.dhananjaysaini.musicplayerapp.databinding.FragmentPlaylistSongsBinding
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.viewmodel.MainViewModel
import com.dhananjaysaini.musicplayerapp.viewmodel.PlaylistViewModel

class PlaylistSongsFragment : Fragment(R.layout.fragment_playlist_songs) {

    private lateinit var binding: FragmentPlaylistSongsBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var playlistVM: PlaylistViewModel
    private lateinit var playlistSongsAdapter: PlaylistSongsAdapter

    private val songs = ArrayList<Music>()
    private var currentSongIds: List<String> = emptyList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPlaylistSongsBinding.bind(view)

        playlistVM = ViewModelProvider(requireActivity()).get(PlaylistViewModel::class.java)
        mainVM = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        playlistSongsAdapter = PlaylistSongsAdapter(requireContext(), songs)

        binding.playlistSongsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistSongsRecycler.adapter = playlistSongsAdapter

        playlistSongsAdapter.onItemClick = { list, position ->
            MusicService.playlist = ArrayList(list)
            MusicService.position = position

            requireContext().startService(
                Intent(requireContext(), MusicService::class.java)
                    .setAction(Constants.ACTION_PLAY_AT)
                    .putExtra("songPosition", position)
            )
            startActivity(Intent(requireContext(), PlayerActivity::class.java)
                .putExtra("source", "playlist"))
        }

        val playlistName = requireArguments().getString("playlistName", "Playlist")
        binding.playlistTitle.text = playlistName

        val playlistId = requireArguments().getInt("playlistId", -1)

        // 1️⃣ Observe playlist song IDs
        playlistVM.getPlaylistSongIds(playlistId)
            .observe(viewLifecycleOwner) { ids ->
                currentSongIds = ids
                updateSongs()
            }

        // 2️⃣ Observe all songs
        mainVM.musicListLiveData
            .observe(viewLifecycleOwner) {
                updateSongs()
            }

        binding.btnBack.setOnClickListener{
            requireActivity().finish()
        }

        removeSong()

        updateTitle()
    }

    private fun updateSongs() {

        val allSongs = MusicService.allSongs   // or MusicService.playlist cache

        if (currentSongIds.isEmpty()) {
            songs.clear()
    //        playlistSongsAdapter.updateList(ArrayList(songs))
            return
        }

        songs.clear()
        songs.addAll(
            allSongs.filter { it.id in currentSongIds }
        )

        playlistSongsAdapter.updateList(ArrayList(songs))

        binding.playlistSongCount.text = when (songs.size){
            0 -> "0 song"
            1 -> "1 song"
            else -> "${songs.size} songs"
        }
    }

    private fun removeSong(){
        playlistSongsAdapter.onRemoveFromPlaylist = { song ->

            val playlistId =
                requireArguments().getInt("playlistId")

            playlistVM.removeSongFromPlaylist(
                playlistId = playlistId,
                songId = song.id
            )
        }

    }

    private fun updateTitle(){

        val playlistId = requireArguments().getInt("playlistId")

        playlistVM.getPlaylistById(playlistId)
            .observe(viewLifecycleOwner) { playlist ->
                binding.playlistTitle.text = playlist.name
            }

    }

}
