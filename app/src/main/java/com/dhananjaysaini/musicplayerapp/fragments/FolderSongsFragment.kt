package com.dhananjaysaini.musicplayerapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.adapter.FolderSongsAdapter
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.databinding.FragmentFolderSongsBinding
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.dhananjaysaini.musicplayerapp.viewmodel.FolderViewModel
import java.io.File

class FolderSongsFragment : Fragment(R.layout.fragment_folder_songs) {

    private lateinit var binding: FragmentFolderSongsBinding
    private lateinit var folderSongsAdapter: FolderSongsAdapter

    private val songs = ArrayList<Music>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFolderSongsBinding.bind(view)

        val folderPath = requireArguments().getString("folderPath") ?: return
        val folderName = requireArguments().getString("folderName")  ?: File(
            requireArguments().getString("folderPath") ?: "").name ?: "Folder"

        binding.folderTitle.text = folderName

        songs.addAll(MusicService.allSongs.filter {
                it.path.startsWith(folderPath)
            }
        )

        folderSongsAdapter = FolderSongsAdapter(requireContext(), songs)

        binding.folderSongsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.folderSongsRecycler.adapter = folderSongsAdapter

        // 🔥 SAME click behaviour everywhere
        folderSongsAdapter.onItemClick = { list, position ->

            MusicService.playlist = list
            MusicService.position = position

            requireContext().startService(
                Intent(requireContext(), MusicService::class.java)
                    .setAction(Constants.ACTION_PLAY_AT)
                    .putExtra("songPosition", position)
            )
            startActivity(Intent(requireContext(), PlayerActivity::class.java))
        }

        binding.btnBack.setOnClickListener{
            requireActivity().finish()
        }
        updateSongs(folderPath)
    }

    private fun updateSongs(folderPath: String) {

        val allSongs = MusicService.allSongs

        songs.clear()
        songs.addAll(
            allSongs.filter { it.path.startsWith(folderPath) }
        )

        folderSongsAdapter.updateList(ArrayList(songs))

        binding.folderSongCount.text = when (songs.size) {
            0 -> "No songs"
            1 -> "1 song"
            else -> "${songs.size} songs"
        }
    }

}
