package com.dhananjaysaini.musicplayerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.adapter.HomeAdapter
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.dao.PlayHistoryDao
import com.dhananjaysaini.musicplayerapp.database.MusicDatabase
import com.dhananjaysaini.musicplayerapp.databinding.FragmentHomeBinding
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.viewmodel.MainViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recentlyPlayedAdapter: HomeAdapter
    private lateinit var topTracksAdapter: HomeAdapter
    private lateinit var recentlyAddedAdapter: HomeAdapter
    private lateinit var mainVM: MainViewModel
    private lateinit var playHistoryDao: PlayHistoryDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        mainVM = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        playHistoryDao = MusicDatabase
            .getDatabase(requireContext())
            .playHistoryDao()

        setupRecyclerViews()
        observeData()
    }

    private fun setupRecyclerViews() {

        recentlyPlayedAdapter = HomeAdapter(requireContext(), arrayListOf())
        topTracksAdapter = HomeAdapter(requireContext(), arrayListOf())
        recentlyAddedAdapter = HomeAdapter(requireContext(), arrayListOf())

        binding.recentRecycler.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL, false
            )

        binding.topRecycler.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL, false
            )

        binding.recentAddedRecycler.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL, false
            )

        binding.recentRecycler.adapter = recentlyPlayedAdapter
        binding.topRecycler.adapter = topTracksAdapter
        binding.recentAddedRecycler.adapter = recentlyAddedAdapter

        recentlyPlayedAdapter.onItemClick = { list, position ->
            playSong(list, position)
        }

        topTracksAdapter.onItemClick = { list, position ->
            playSong(list, position)
        }

        recentlyAddedAdapter.onItemClick = { list, position ->
            playSong(list, position)
        }
    }

    private fun observeData() {

        mainVM.musicListLiveData.observe(viewLifecycleOwner) { allSongs ->

            playHistoryDao.getRecentlyPlayed()
                .observe(viewLifecycleOwner) { historyList ->

                    val recentSongs = historyList.mapNotNull { history ->
                        allSongs.find { it.id == history.songId }
                    }

                    recentlyPlayedAdapter.updateList(ArrayList(recentSongs))
                }

            playHistoryDao.getTopPlayed()
                .observe(viewLifecycleOwner) { historyList ->

                    val topSongs = historyList.mapNotNull { history ->
                        allSongs.find { it.id == history.songId }
                    }

                    topTracksAdapter.updateList(ArrayList(topSongs))
                }
        }

        mainVM.musicListLiveData.observe(viewLifecycleOwner) { allSongs ->

            val recentlyAdded = allSongs
                .sortedByDescending { it.date }
                .take(20)

            recentlyAddedAdapter.updateList(ArrayList(recentlyAdded))
        }
    }

    private fun playSong(list: ArrayList<Music>, position: Int) {

        MusicService.playlist = ArrayList(list)
        MusicService.position = position

        requireContext().startService(
            Intent(requireContext(), MusicService::class.java)
                .setAction(Constants.ACTION_PLAY_AT)
                .putExtra("songPosition", position)
        )
        startActivity(Intent(requireContext(), PlayerActivity::class.java))
    }
}
