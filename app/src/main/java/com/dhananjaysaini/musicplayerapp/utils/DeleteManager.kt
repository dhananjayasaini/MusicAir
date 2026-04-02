package com.dhananjaysaini.musicplayerapp.utils

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dhananjaysaini.musicplayerapp.model.Music

object DeleteManager {

    const val DELETE_REQUEST_CODE = 1001

    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteFromDevice(activity: Activity, song: Music) {

        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            song.id.toLong()
        )

        try {

            activity.contentResolver.delete(uri, null, null)

            Toast.makeText(activity, "Song deleted", Toast.LENGTH_SHORT).show()

            activity.sendBroadcast(Intent("REFRESH_LIBRARY"))

        } catch (e: RecoverableSecurityException) {

            val intentSender = e.userAction.actionIntent.intentSender

            activity.startIntentSenderForResult(
                intentSender,
                DELETE_REQUEST_CODE,
                null,
                0,
                0,
                0
            )

        } catch (e: Exception) {

            Toast.makeText(activity, "Unable to delete", Toast.LENGTH_SHORT).show()
        }
    }
}