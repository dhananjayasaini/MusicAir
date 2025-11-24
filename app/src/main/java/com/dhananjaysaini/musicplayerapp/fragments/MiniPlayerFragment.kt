package com.dhananjaysaini.musicplayerapp.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.activities.PlayerActivity
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.service.MusicService

class MiniPlayerFragment : Fragment() {

    private lateinit var miniArt: ImageView
    private lateinit var miniTitle: TextView
    private lateinit var miniArtist: TextView
    private lateinit var miniPlay: ImageButton
    private lateinit var miniNext: ImageButton
    private lateinit var miniPrev: ImageButton
    private lateinit var miniSeekbar: SeekBar
    private lateinit var rootView: View

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "UPDATE_UI") {
                val isPlaying = intent.getBooleanExtra("isPlaying", false)
                val title = intent.getStringExtra("title") ?: "Unknown"
                val artist = intent.getStringExtra("artist") ?: ""
                val artUri = intent.getStringExtra("artUri")
                val currentMs = intent.getIntExtra("currentMs", 0)
                val durationMs = intent.getIntExtra("durationMs", 0)

                miniTitle.text = title
                miniArtist.text = artist
                miniPlay.setImageResource(if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
                miniSeekbar.max = durationMs
                miniSeekbar.progress = currentMs

                // load artwork safely
                if (!artUri.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(Uri.parse(artUri))
                        .placeholder(R.drawable.itunes)
                        .into(miniArt)
                } else {
                    miniArt.setImageResource(R.drawable.itunes)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_mini_player, container, false)
        initViews()
        setupListeners()
        return rootView
    }

    private fun initViews() {
        miniArt = rootView.findViewById(R.id.mini_art)
        miniTitle = rootView.findViewById(R.id.mini_title)
        miniArtist = rootView.findViewById(R.id.mini_artist)
        miniPlay = rootView.findViewById(R.id.mini_play)
        miniNext = rootView.findViewById(R.id.mini_next)
        miniPrev = rootView.findViewById(R.id.mini_prev)
        miniSeekbar = rootView.findViewById(R.id.mini_seekbar)
    }

    private fun setupListeners() {
        miniPlay.setOnClickListener {
            val intent = Intent(requireContext(), MusicService::class.java)
            intent.action = Constants.ACTION_PLAY
            requireContext().startService(intent)
        }

        miniNext.setOnClickListener {
            val intent = Intent(requireContext(), MusicService::class.java)
            intent.action = Constants.ACTION_NEXT
            requireContext().startService(intent)
        }

        miniPrev.setOnClickListener {
            val intent = Intent(requireContext(), MusicService::class.java)
            intent.action = Constants.ACTION_PREVIOUS
            requireContext().startService(intent)
        }

        // Open full player
        rootView.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            startActivity(intent)
        }

        miniSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {
                val seekTo = sb?.progress ?: 0
                val intent = Intent(requireContext(), MusicService::class.java)
                intent.action = Constants.ACTION_SEEK_TO
                intent.putExtra("seekToMs", seekTo)
                requireContext().startService(intent)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("UPDATE_UI")
        requireActivity().registerReceiver(updateReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(updateReceiver)
    }
}
