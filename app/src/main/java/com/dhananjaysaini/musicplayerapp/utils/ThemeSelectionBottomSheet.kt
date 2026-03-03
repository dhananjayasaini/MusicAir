package com.dhananjaysaini.musicplayerapp.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.adapter.ThemeAdapter
import com.dhananjaysaini.musicplayerapp.databinding.FragmentThemeSelectionBinding
import com.dhananjaysaini.musicplayerapp.model.AppTheme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ThemeSelectionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentThemeSelectionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentThemeSelectionBinding.inflate(inflater, container, false)

        val themeList = listOf(
            AppTheme(0, "Default", R.drawable.theme0),
            AppTheme(1, "Sunset Glow", R.drawable.theme1),
            AppTheme(2, "Ocean Blue", R.drawable.theme2),
            AppTheme(3, "Purple Night", R.drawable.theme3),
            AppTheme(4, "Cherry Blossom", R.drawable.theme4),
            AppTheme(5, "Grey Shade", R.drawable.theme5),
            AppTheme(6, "Midnight Black", R.drawable.theme6),
            AppTheme(7, "Pale Wood", R.drawable.theme7),
            AppTheme(8, "Frost", R.drawable.theme8),
            AppTheme(9, "Gold Royal", R.drawable.theme9),
            AppTheme(10, "Emerald Green", R.drawable.theme10) ,
            AppTheme(11, "Orange", R.drawable.theme11)

        )

        val themeAdapter = ThemeAdapter(requireContext(), themeList)

        binding.themeRecycler.layoutManager = GridLayoutManager(requireContext(),2)
//            LinearLayoutManager(requireContext(),
//            LinearLayoutManager.HORIZONTAL, false )

        binding.themeRecycler.adapter = themeAdapter

        themeAdapter.onThemeClick = { theme ->

            ThemeManager.saveTheme(requireContext(), theme.id)
      //     requireActivity().recreate()
            ThemeManager.applyThemeToActivity(requireActivity())
            dismiss()
        }

        return binding.root
    }
}