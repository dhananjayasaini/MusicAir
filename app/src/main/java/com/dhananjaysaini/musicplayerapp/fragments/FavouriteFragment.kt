package com.dhananjaysaini.musicplayerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.adapter.FavouriteAdapter
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.databinding.FragmentFavouriteBinding
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.viewmodel.FavouriteViewModel
import com.dhananjaysaini.musicplayerapp.viewmodel.MainViewModel


class FavouriteFragment : Fragment(R.layout.fragment_favourite) {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var favAdapter: FavouriteAdapter
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var mainViewModel: MainViewModel

    private val favouriteSongs = ArrayList<Music>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouriteBinding.bind(view)

        favouriteViewModel = ViewModelProvider(requireActivity()).get(FavouriteViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)


        setupRecyclerView()
        observeFavorites()


    }

    private fun setupRecyclerView() {

        favAdapter = FavouriteAdapter(requireContext(), favouriteSongs)

        binding.favRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favAdapter
            setHasFixedSize(true)
        }

        // ▶ CLICK → PLAY FAVORITE PLAYLIST
        favAdapter.onItemClick = { list, position ->

            if (list.isNotEmpty()) {

            MusicService.playlist = ArrayList(list)
            MusicService.position = position

            requireContext().startService(
                Intent(requireContext(), MusicService::class.java).apply {
                    action = Constants.ACTION_PLAY_AT
                    putExtra("songPosition", position)
                }
            )

                startActivity(
                    Intent(requireContext(), PlayerActivity::class.java)
                )
        }
        }
    }

    private fun observeFavorites() {

        favouriteViewModel.favouriteIds.observe(viewLifecycleOwner) { favIds ->

            mainViewModel.musicListLiveData.observe(viewLifecycleOwner) { allSongs ->

                favouriteSongs.clear()
                favouriteSongs.addAll(
                    allSongs.filter { it.id in favIds }
                )
                Log.d("favsongs", "favsongs1 " + allSongs.toString())
                favAdapter.updateList(ArrayList(favouriteSongs))

                Log.d("favsongs", "favsongs2 " + favouriteSongs)
                Log.d("favsongs", "favsongs3 " + favAdapter.toString())

                if(favouriteSongs.isEmpty())
                    binding.favFragment.visibility = View.VISIBLE
                else
                    binding.favFragment.visibility = View.GONE

            }

        }
    }

}


