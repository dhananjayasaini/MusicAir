package com.dhananjaysaini.musicplayerapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.view.LayoutInflater
import android.widget.*
import com.dhananjaysaini.musicplayerapp.databinding.EqualizerLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class EqualizerManager(private val context: Context) {

    private lateinit var binding: EqualizerLayoutBinding

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var reverb: PresetReverb? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    fun showEqualizer(audioSessionId: Int) {

        val dialog = BottomSheetDialog(context)

        binding = EqualizerLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        if (audioSessionId == -1) {
            Toast.makeText(context, "Audio Session not available!", Toast.LENGTH_SHORT).show()
            return
        }

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
        }

        // Back button
        binding.btnBack.setOnClickListener {
            dialog.dismiss()
        }

        // Equalizer ON/OFF
        binding.switchEqualizer.isChecked = equalizer?.enabled == true

        binding.switchEqualizer.setOnCheckedChangeListener { _, isChecked ->
            equalizer?.enabled = isChecked
            bassBoost?.enabled = isChecked
            virtualizer?.enabled = isChecked
            reverb?.enabled = isChecked
        }

        // Presets
        val presetNames = ArrayList<String>()
        for (i in 0 until equalizer!!.numberOfPresets) {
            presetNames.add(equalizer!!.getPresetName(i.toShort()))
        }

        val presetAdapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, presetNames)
        presetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerPreset.adapter = presetAdapter

        binding.spinnerPreset.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                equalizer?.usePreset(position.toShort())

                updateBandProgress(equalizer!!, 0, binding.seekBand60, binding.tvBand60)
                updateBandProgress(equalizer!!, 1, binding.seekBand230, binding.tvBand230)
                updateBandProgress(equalizer!!, 2, binding.seekBand910, binding.tvBand910)
                updateBandProgress(equalizer!!, 3, binding.seekBand3600, binding.tvBand3600)
                updateBandProgress(equalizer!!, 4, binding.seekBand14000, binding.tvBand14000)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Setup Bands
        setupBand(equalizer!!, 0, binding.seekBand60, binding.tvBand60)
        setupBand(equalizer!!, 1, binding.seekBand230, binding.tvBand230)
        setupBand(equalizer!!, 2, binding.seekBand910, binding.tvBand910)
        setupBand(equalizer!!, 3, binding.seekBand3600, binding.tvBand3600)
        setupBand(equalizer!!, 4, binding.seekBand14000, binding.tvBand14000)

        // BassBoost Setup
        binding.seekBass.max = 1000
        binding.seekBass.progress = bassBoost?.roundedStrength?.toInt() ?: 0

        binding.seekBass.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bassBoost?.setStrength(progress.toShort())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Virtualizer Setup
        binding.seekVirtualizer.max = 1000
        binding.seekVirtualizer.progress = virtualizer?.roundedStrength?.toInt() ?: 0

        binding.seekVirtualizer.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                virtualizer?.setStrength(progress.toShort())
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

        val reverbAdapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, reverbPresets)
        reverbAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerReverb.adapter = reverbAdapter

        binding.spinnerReverb.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
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
    }

    private fun setupBand(equalizer: Equalizer, bandIndex: Int, bar: EQVerticalBar, tv: TextView) {

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
