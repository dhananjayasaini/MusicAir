package com.dhananjaysaini.musicplayerapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.dhananjaysaini.musicplayerapp.R
import com.dhananjaysaini.musicplayerapp.databinding.EqualizerLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class EqualizerManager(private val context: Context) {

    private lateinit var binding: EqualizerLayoutBinding
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var reverb: PresetReverb? = null
 //   private var musicService = MusicService


    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    fun showEqualizer(audioSessionId: Int) {

        if (audioSessionId == -1) {
            Toast.makeText(context, "Audio Session not available!", Toast.LENGTH_SHORT).show()
            return
        }

        // Init Audio Effects
        if (equalizer == null) {
            equalizer = Equalizer(0, audioSessionId)
            equalizer?.enabled = true
        }

        if (bassBoost == null) {
            bassBoost = BassBoost(0, audioSessionId)
            bassBoost?.enabled = true
        }

        if (virtualizer == null) {
            virtualizer = Virtualizer(0, audioSessionId)
            virtualizer?.enabled = true
        }

        if (reverb == null) {
            reverb = PresetReverb(0, audioSessionId)
            reverb?.enabled = true
          //  musicService.applyReverbEffect(reverb!!.id)

        }

        val dialog = BottomSheetDialog(context)
        val view: View = LayoutInflater.from(context).inflate(R.layout.equalizer_layout, null)
        dialog.setContentView(view)

        val switchEqualizer = view.findViewById<Switch>(R.id.switchEqualizer)
        val spinnerPreset = view.findViewById<Spinner>(R.id.spinnerPreset)

        val seekBass = view.findViewById<SeekBar>(R.id.seekBass)
        val seekVirtualizer = view.findViewById<SeekBar>(R.id.seekVirtualizer)
        val spinnerReverb = view.findViewById<Spinner>(R.id.spinnerReverb)

        // Fixed Band Seekbars
        val seekBand60 = view.findViewById<EQVerticalBar>(R.id.seekBand60)
        val seekBand230 = view.findViewById<EQVerticalBar>(R.id.seekBand230)
        val seekBand910 = view.findViewById<EQVerticalBar>(R.id.seekBand910)
        val seekBand3600 = view.findViewById<EQVerticalBar>(R.id.seekBand3600)
        val seekBand14000 = view.findViewById<EQVerticalBar>(R.id.seekBand14000)


        // Band TextViews (top values)
        val tvBand60 = view.findViewById<TextView>(R.id.tvBand60)
        val tvBand230 = view.findViewById<TextView>(R.id.tvBand230)
        val tvBand910 = view.findViewById<TextView>(R.id.tvBand910)
        val tvBand3600 = view.findViewById<TextView>(R.id.tvBand3600)
        val tvBand14000 = view.findViewById<TextView>(R.id.tvBand14000)

        // Equalizer ON/OFF
        switchEqualizer.isChecked = equalizer?.enabled == true

        switchEqualizer.setOnCheckedChangeListener { _, isChecked ->
            equalizer?.enabled = isChecked
            bassBoost?.enabled = isChecked
            virtualizer?.enabled = isChecked
            reverb?.enabled = isChecked
        }

        // Setup Presets
        val presetNames = ArrayList<String>()
        for (i in 0 until equalizer!!.numberOfPresets) {
            presetNames.add(equalizer!!.getPresetName(i.toShort()))
        }

        val presetAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, presetNames)
        presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPreset.adapter = presetAdapter

        spinnerPreset.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                equalizer?.usePreset(position.toShort())

                updateBandProgress(equalizer!!, 0, seekBand60, tvBand60)
                updateBandProgress(equalizer!!, 1, seekBand230, tvBand230)
                updateBandProgress(equalizer!!, 2, seekBand910, tvBand910)
                updateBandProgress(equalizer!!, 3, seekBand3600, tvBand3600)
                updateBandProgress(equalizer!!, 4, seekBand14000, tvBand14000)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Setup Bands
        setupBand(equalizer!!, 0, seekBand60, tvBand60)
        setupBand(equalizer!!, 1, seekBand230, tvBand230)
        setupBand(equalizer!!, 2, seekBand910, tvBand910)
        setupBand(equalizer!!, 3, seekBand3600, tvBand3600)
        setupBand(equalizer!!, 4, seekBand14000, tvBand14000)

        // BassBoost Setup
        seekBass.max = 1000
        seekBass.progress = bassBoost?.roundedStrength?.toInt() ?: 0

        seekBass.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bassBoost?.setStrength(progress.toShort())

                bassBoost?.strengthSupported?.let { supported ->
                    if (!supported) {
                        Toast.makeText(context, "BassBoost not supported", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Virtualizer Setup
        seekVirtualizer.max = 1000
        seekVirtualizer.progress = virtualizer?.roundedStrength?.toInt() ?: 0

        seekVirtualizer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                virtualizer?.setStrength(progress.toShort())

                virtualizer?.strengthSupported?.let { supported ->
                    if (!supported) {
                        Toast.makeText(context, "Virtualizer not supported", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Reverb Presets
        val reverbPresets = listOf(
            "None",
            "Small Room",
            "Medium Room",
            "Large Room",
            "Medium Hall",
            "Large Hall",
            "Plate"
        )

        val reverbAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, reverbPresets)
        reverbAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerReverb.adapter = reverbAdapter

        spinnerReverb.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> reverb?.preset = PresetReverb.PRESET_NONE
                    1 -> reverb?.preset = PresetReverb.PRESET_SMALLROOM
                    2 -> reverb?.preset = PresetReverb.PRESET_MEDIUMROOM
                    3 -> reverb?.preset = PresetReverb.PRESET_LARGEROOM
                    4 -> reverb?.preset = PresetReverb.PRESET_MEDIUMHALL
                    5 -> reverb?.preset = PresetReverb.PRESET_LARGEHALL
                    6 -> reverb?.preset = PresetReverb.PRESET_PLATE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        dialog.show()

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener{
            dialog.dismiss()
        }
    }

    private fun setupBand(
        equalizer: Equalizer,
        bandIndex: Int,
        bar: EQVerticalBar,
        tv: TextView
    ) {

        if (bandIndex >= equalizer.numberOfBands) {
            tv.text = "N/A"
            bar.setProgress(0)
            return
        }

        val minLevel = equalizer.bandLevelRange[0]
        val maxLevel = equalizer.bandLevelRange[1]

        bar.setMax(maxLevel - minLevel)

        val currentLevel = equalizer.getBandLevel(bandIndex.toShort())
        val progress = currentLevel - minLevel

        bar.setProgress(progress)

        tv.text = (currentLevel / 100).toString()

        bar.onProgressChanged = { value ->
            val level = (value + minLevel).toShort()
            equalizer.setBandLevel(bandIndex.toShort(), level)
            tv.text = (level / 100).toString()
        }
    }

    private fun updateBandProgress(
        equalizer: Equalizer,
        bandIndex: Int,
        bar: EQVerticalBar,
        tv: TextView
    ) {
        if (bandIndex >= equalizer.numberOfBands) return

        val minLevel = equalizer.bandLevelRange[0]
        val currentLevel = equalizer.getBandLevel(bandIndex.toShort())

        bar.setProgress(currentLevel - minLevel)
        tv.text = (currentLevel / 100).toString()
    }


    fun release() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        reverb?.release()

        equalizer = null
        bassBoost = null
        virtualizer = null
        reverb = null
    }
}
