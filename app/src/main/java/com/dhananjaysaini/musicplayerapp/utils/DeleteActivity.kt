package com.dhananjaysaini.musicplayerapp.utils

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.content.IntentSender
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.dhananjaysaini.musicplayerapp.model.Music

class DeleteHandler(
    private val activity: Activity,
    private val contentResolverContext: Context,
    private val launcher: ActivityResultLauncher<IntentSenderRequest>,
    private val onSongDeleted: (position: Int) -> Unit
) {

    private var pendingDeleteSong: Music? = null
    private var pendingDeletePosition: Int = -1

    fun deleteSong(song: Music, position: Int) {
        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            song.id.toLong()
        )

        try {
            val rowsDeleted = contentResolverContext.contentResolver.delete(uri, null, null)
            if (rowsDeleted > 0) {
                onSongDeleted(position)
                Toast.makeText(activity, "${song.title} deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Failed to delete ${song.title}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
                pendingDeleteSong = song
                pendingDeletePosition = position

                val intentSender: IntentSender = e.userAction.actionIntent.intentSender
                val request = IntentSenderRequest.Builder(intentSender).build()
                launcher.launch(request)
            } else {
                Toast.makeText(activity, "Cannot delete ${song.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onActivityResult(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK && pendingDeleteSong != null && pendingDeletePosition != -1) {
            deleteSong(pendingDeleteSong!!, pendingDeletePosition)
            pendingDeleteSong = null
            pendingDeletePosition = -1
        } else {
            Toast.makeText(activity, "Delete permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
