package com.dhananjaysaini.musicplayerapp.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.databinding.FragmentMiniPlayerBinding
import com.dhananjaysaini.musicplayerapp.service.MusicService

class MiniPlayerFragment : Fragment(R.layout.fragment_mini_player) {

    private lateinit var binding: FragmentMiniPlayerBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMiniPlayerBinding.bind(view)

        setupClicks()
    }

    override fun onStart() {
        super.onStart()
        ContextCompat.registerReceiver(
            requireContext(),
            updateReceiver,
            IntentFilter("UPDATE_UI"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        requireContext().sendBroadcast(Intent("REQUEST_UI_UPDATE"))

    }

    override fun onStop() {
        super.onStop()
        try {
            requireContext().unregisterReceiver(updateReceiver)
        }
        catch (_: Exception) { }
    }

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val title = intent?.getStringExtra("title") ?: return
            val artist = intent.getStringExtra("artist") ?: ""
            val isPlaying = intent.getBooleanExtra("isPlaying", false)
            val artUri = intent.getStringExtra("artUri")

            binding.miniTitle.text = title
            binding.miniArtist.text = artist

            binding.miniPlayPause.setImageResource(
                if (isPlaying) R.drawable.pause_icon
                else R.drawable.play_icon
            )

            Glide.with(this@MiniPlayerFragment)
                .load(artUri)
                .placeholder(R.drawable.ic_music)
                .into(binding.miniArt)
        }
    }

    private fun setupClicks() {

        binding.miniPlayPause.setOnClickListener {
            val action =
                if (MusicService.mediaPlayer?.isPlaying == true)
                    Constants.ACTION_PAUSE
                else
                    Constants.ACTION_PLAY

            requireContext().startService(
                Intent(requireContext(), MusicService::class.java)
                    .setAction(action)
            )
        }

        binding.miniNext.setOnClickListener {
            requireContext().startService(
                Intent(requireContext(), MusicService::class.java)
                    .setAction(Constants.ACTION_NEXT)
            )
        }

        binding.miniPrev.setOnClickListener {
            requireContext().startService(
                Intent(requireContext(), MusicService::class.java)
                    .setAction(Constants.ACTION_PREVIOUS)
            )
        }

        binding.root.setOnClickListener {
            startActivity(
                Intent(requireContext(), PlayerActivity::class.java)
                    .putExtra("class", "MiniPlayer")
            )
        }
    }
}
