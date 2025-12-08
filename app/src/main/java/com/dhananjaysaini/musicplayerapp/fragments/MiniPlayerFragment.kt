    package com.dhananjaysaini.musicplayerapp.fragments

    import android.content.BroadcastReceiver
    import android.content.Context
    import android.content.Intent
    import android.content.IntentFilter
    import android.net.Uri
    import android.os.Bundle
    import android.os.Handler
    import android.os.Looper
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

                    val idx = intent.getIntExtra("Index", MusicService.position)
                    MusicService.position = idx

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
                val action =
                    if (MusicService.mediaPlayer?.isPlaying == true)
                        Constants.ACTION_PAUSE
                    else
                        Constants.ACTION_PLAY

                val intent = Intent(requireContext(), MusicService::class.java)
                intent.action = action
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
                intent.putExtra("index", MusicService.position)
                intent.putExtra("class", "MiniPlayer")
                intent.putExtra("musicList", ArrayList(MusicService.playlist))
                startActivity(intent)
            }

/*            miniSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {}
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {
                    val seekTo = sb?.progress ?: 0
                    val intent = Intent(requireContext(), MusicService::class.java)
                    intent.action = Constants.ACTION_SEEK_TO
                    intent.putExtra("seekToMs", seekTo)
                    requireContext().startService(intent)
                }
            })*/
        }

        private fun updateUiFromService() {
            try {
                val pos = MusicService.position
                val list = MusicService.playlist
                val song = list.getOrNull(pos)

                // if no song, hide or set defaults
                if (song == null) {
                    miniTitle.text = "No Song"
                    miniArtist.text = ""
                    miniArt.setImageResource(R.drawable.itunes)
                    miniSeekbar.max = 0
                    miniSeekbar.progress = 0
                    miniPlay.setImageResource(android.R.drawable.ic_media_play)
                    return
                }

                val isPlaying = MusicService.mediaPlayer?.isPlaying == true
                val currentMs = MusicService.mediaPlayer?.currentPosition ?: 0
                val durationMs = MusicService.mediaPlayer?.duration ?: 0

                miniTitle.text = song.title
                miniArtist.text = song.artist
                miniPlay.setImageResource(if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
                miniSeekbar.max = if (durationMs > 0) durationMs else 0
                miniSeekbar.progress = currentMs.coerceAtLeast(0)

                if (!song.artUri.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(Uri.parse(song.artUri))
                        .placeholder(R.drawable.itunes)
                        .into(miniArt)
                } else {
                    miniArt.setImageResource(R.drawable.itunes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onResume() {
            super.onResume()

            updateUiFromService()
            val filter = IntentFilter("UPDATE_UI")
            requireActivity().registerReceiver(updateReceiver, filter)

            requireContext().sendBroadcast(Intent("REQUEST_UI_UPDATE"))


        }

        override fun onPause() {
            super.onPause()
            requireActivity().unregisterReceiver(updateReceiver)
        }
    }
