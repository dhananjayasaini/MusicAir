package com.dhananjaysaini.musicplayerapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dhananjaysaini.musicplayerapp.fragments.AlbumFragment
import com.dhananjaysaini.musicplayerapp.fragments.AllSongsFragment
import com.dhananjaysaini.musicplayerapp.fragments.ArtistFragment
import com.dhananjaysaini.musicplayerapp.fragments.FavouriteFragment
import com.dhananjaysaini.musicplayerapp.fragments.FolderFragment
import com.dhananjaysaini.musicplayerapp.fragments.HomeFragment
import com.dhananjaysaini.musicplayerapp.fragments.PlaylistFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {

        return when(position) {
        //    0 -> HomeFragment()
            0 -> AllSongsFragment()
            1 -> FavouriteFragment()
            2 -> PlaylistFragment()
            3 -> ArtistFragment()
            4 -> AlbumFragment()
            5 -> FolderFragment()
            else -> AllSongsFragment()
        }
    }
}