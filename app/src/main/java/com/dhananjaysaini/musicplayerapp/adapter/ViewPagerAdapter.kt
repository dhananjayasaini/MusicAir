package com.dhananjaysaini.musicplayerapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dhananjaysaini.musicplayerapp.fragments.AllSongsFragment
import com.dhananjaysaini.musicplayerapp.fragments.FavouriteFragment
import com.dhananjaysaini.musicplayerapp.fragments.FolderFragment
import com.dhananjaysaini.musicplayerapp.fragments.HomeFragment
import com.dhananjaysaini.musicplayerapp.fragments.PlaylistFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {

        return when(position) {
            0 -> HomeFragment()
            1 -> AllSongsFragment()
            2 -> FavouriteFragment()
            3 -> PlaylistFragment()
            4 -> FolderFragment()
            else -> HomeFragment()
        }
    }
}