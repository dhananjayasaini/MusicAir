package com.dhananjaysaini.musicplayerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.adapter.MainAdapter
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.databinding.FragmentAllSongsBinding
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.viewmodel.MainViewModel

class AllSongsFragment : Fragment() {

    private lateinit var binding: FragmentAllSongsBinding
    private lateinit var adapter: MainAdapter

    private val viewModel: MainViewModel by activityViewModels()

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
    }

    private fun setupRecyclerView() {
        adapter = MainAdapter(requireContext(), ArrayList())

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
        viewModel.musicListLiveData.observe(viewLifecycleOwner) { list ->
            binding.totalSongsTv.text = "${list.size} Songs"
            adapter.updateList(ArrayList(list))
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
}
