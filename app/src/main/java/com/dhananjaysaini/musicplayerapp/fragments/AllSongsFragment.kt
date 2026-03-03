package com.dhananjaysaini.musicplayerapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.adapter.AllSongsAdapter
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.databinding.FragmentAllSongsBinding
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.utils.ThemeManager
import com.dhananjaysaini.musicplayerapp.viewmodel.MainViewModel
import com.dhananjaysaini.musicplayerapp.viewmodel.PlaylistViewModel

class AllSongsFragment : Fragment() {

    private lateinit var binding: FragmentAllSongsBinding
    private lateinit var adapter: AllSongsAdapter

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeSongs()

        adapter.onAddToPlaylist = { song ->
            showPlaylistChooser(song)
        }
    }

    private fun setupRecyclerView() {
        adapter = AllSongsAdapter(requireContext(), ArrayList())

        binding.musicRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AllSongsFragment.adapter
            setHasFixedSize(true)
        }

        adapter.onItemClick = { list, position ->
            playSong(list, position)
        }
    }

    private fun observeSongs() {
        mainViewModel.musicListLiveData.observe(viewLifecycleOwner) { list ->
            adapter.updateList(ArrayList(list))

            binding.totalSongsTv.text =
                if (list.size == 1)
                    "1 Song"
                else
                    "${list.size} Songs"
        }
    }

    private fun playSong(list: ArrayList<Music>, position: Int) {

        MusicService.playlist = ArrayList(list)
        MusicService.position = position

        requireContext().startService(
            Intent(requireContext(), MusicService::class.java).apply {
                action = Constants.ACTION_PLAY_AT
                putExtra("songPosition", position)
            }
        )

        requireContext().sendBroadcast(Intent("SHOW_MINI_PLAYER"))

        startActivity(
            Intent(requireContext(), PlayerActivity::class.java)
        )
    }

    private fun showPlaylistChooser(song: Music) {

        val playlistVM = ViewModelProvider(requireActivity()).get(PlaylistViewModel::class.java)

        playlistVM.playlists.observe(viewLifecycleOwner) { playlists ->

            if (playlists.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Create a playlist first",
                    Toast.LENGTH_SHORT
                ).show()
                return@observe
            }

            val names = playlists.map { it.name }.toTypedArray()

            AlertDialog.Builder(requireContext())
                .setTitle("Add to playlist")
                .setItems(names) { _, index ->
                    playlistVM.addSongToPlaylist(
                        playlists[index].songId,
                        song.id
                    )

                    Log.d("PLAYLIST_ADD", "Adding song ${song.id} to playlist ${playlists[index].songId}")
                }.show()
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeManager.applyThemeToActivity(requireActivity())
    }

}
