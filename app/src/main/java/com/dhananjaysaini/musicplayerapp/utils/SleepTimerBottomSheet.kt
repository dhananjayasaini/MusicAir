package com.dhananjaysaini.musicplayerapp.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dhananjaysaini.musicplayerapp.databinding.BottomSheetSleepTimerBinding
import com.dhananjaysaini.musicplayerapp.service.MusicService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SleepTimerBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSleepTimerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        binding = BottomSheetSleepTimerBinding.inflate(inflater, container, false)

        setupPickers()
        setupClicks()

        return binding.root
    }

    private fun setupPickers() {

        binding.npHours.minValue = 0
        binding.npHours.maxValue = 23
        binding.npHours.wrapSelectorWheel = true

        binding.npMinutes.minValue = 0
        binding.npMinutes.maxValue = 59
        binding.npMinutes.wrapSelectorWheel = true

        binding.npSeconds.minValue = 0
        binding.npSeconds.maxValue = 59
        binding.npSeconds.wrapSelectorWheel = true
    }

    private fun setupClicks() {

        binding.btn15.setOnClickListener { setTime(0, 15, 0) }
        binding.btn30.setOnClickListener { setTime(0, 30, 0) }
        binding.btn45.setOnClickListener { setTime(0, 45, 0) }
        binding.btn60.setOnClickListener { setTime(1, 0, 0) }

        binding.btnCancel.setOnClickListener {
            MusicService.musicService?.cancelSleepTimer()
            dismiss()
        }

        binding.btnStart.setOnClickListener {
            val totalMillis = getTotalMillis()

            if (totalMillis <= 0) {
                Toast.makeText(requireContext(), "Please select time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            MusicService.musicService?.startSleepTimer(totalMillis)
            Toast.makeText(requireContext(), "Sleep Timer Started", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun setTime(hours: Int, minutes: Int, seconds: Int) {
        binding.npHours.value = hours
        binding.npMinutes.value = minutes
        binding.npSeconds.value = seconds
    }

    private fun getTotalMillis(): Long {

        val hrs = binding.npHours.value
        val min = binding.npMinutes.value
        val sec = binding.npSeconds.value

        return ((hrs * 3600L) + (min * 60L) + sec) * 1000L
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
