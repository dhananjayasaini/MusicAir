package com.dhananjaysaini.musicplayerapp.utils

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dhananjaysaini.musicplayerapp.databinding.BottomSheetSongOptionsBinding
import com.dhananjaysaini.musicplayerapp.model.Music
import com.dhananjaysaini.musicplayerapp.model.SongMenuConfig
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SongOptionsBottomSheet(
    private val song: Music,
    private val config: SongMenuConfig,

) : BottomSheetDialogFragment() {

    private var binding: BottomSheetSongOptionsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = BottomSheetSongOptionsBinding.inflate(inflater, container, false)

        binding!!.btnAddPlaylist.visibility = if (config.showAddToPlaylist) View.VISIBLE else View.GONE

        binding!!.btnRemoveFav.visibility = if (config.showRemoveFromFav) View.VISIBLE else View.GONE

        binding!!.btnRemovePlaylist.visibility = if (config.showRemoveFromPlaylist) View.VISIBLE else View.GONE

        binding!!.btnDeleteSong.visibility = if (config.showDelete) View.VISIBLE else View.GONE

//      binding.btnShare.visibility = if (config.showShare) View.VISIBLE else View.GONE

        clickListeners()
        return binding!!.root
    }

    private fun clickListeners() {

        binding!!.btnAddPlaylist.setOnClickListener {

            SongMenuManager.handleAddToPlaylist(parentFragmentManager, song)
            dismiss()
        }

        binding!!.btnAddFav.setOnClickListener {

            SongMenuManager.handleAddToFavourite(parentFragmentManager, song)
            dismiss()
        }

        binding!!.btnRemoveFav.setOnClickListener {

            SongMenuManager.handleRemoveFromFavourite(requireContext(), song)
            dismiss()
        }

        binding!!.btnRemovePlaylist.setOnClickListener {

            SongMenuManager.handleRemoveFromPlaylist(requireContext(), song)
            dismiss()
        }

        binding!!.btnDeleteSong.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                SongMenuManager.handleDelete(requireContext(), song)
            }
            dismiss()
        }

        binding!!.btnShare.setOnClickListener {

            SongMenuManager.handleShare(requireContext(), song)
            dismiss()
        }
    }
}