package com.dhananjaysaini.musicplayerapp.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.dhananjaysaini.musicplayerapp.activities.MainActivity
import com.dhananjaysaini.musicplayerapp.constants.Constants
import com.dhananjaysaini.musicplayerapp.service.MusicService


class VoiceControlManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    private var speechIntent: Intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")  // For Hinglish use "hi-IN"
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

        }

    init {
        setupListener()
    }

    private fun setupListener() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: Bundle?) {
                val spoken = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.lowercase()

                handleCommand(spoken)
            }

            override fun onError(error: Int) {
                    Log.d("VOICE", "SpeechRecognizer Error: $error")
            }

            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("VOICE", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d("VOICE", "Ready for speech")
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        speechRecognizer.startListening(speechIntent)
    }

    private fun handleCommand(text: String?) {
        if (text.isNullOrEmpty()) return

        when {
            "play" in text ->     if (MainActivity.musicListMA.isNotEmpty()) {
                sendAction(Constants.ACTION_PLAY) }
            "pause" in text -> sendAction(Constants.ACTION_PAUSE)
            "next" in text || "agla" in text -> sendAction(Constants.ACTION_NEXT)
            "back" in text || "pichla" in text -> sendAction(Constants.ACTION_PREVIOUS)
          //  "stop" in text -> sendAction(Constants.ACTION_STOP)

            text.startsWith("play ") ->
                searchAndPlay(text.replace("play ", "").trim())

            text.contains("chalao") ->
                searchAndPlay(text.replace("chalao", "").trim())
        }
    }

    private fun sendAction(action: String) {
        if (MainActivity.musicListMA.isEmpty()) {
            // Prevent crash
            return
        }

        val intent = Intent(context, MusicService::class.java)
        intent.action = action
        context.startService(intent)
    }

    private fun searchAndPlay(name: String) {
        val list = MainActivity.musicListMA

        val index = list.indexOfFirst {
            it.title.lowercase().contains(name.lowercase())
        }

        if (index != -1) {
            val intent = Intent(context, MusicService::class.java).apply {
                action = Constants.ACTION_PLAY
                putExtra("Index", index)
            }
            context.startService(intent)
        }
    }
}
