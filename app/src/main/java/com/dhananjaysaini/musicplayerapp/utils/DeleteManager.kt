package com.dhananjaysaini.musicplayerapp.utils

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dhananjaysaini.musicplayerapp.model.Music

object DeleteManager {

    private const val DELETE_REQUEST_CODE = 1001

    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteSong(context: Context, song: Music) {

        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            song.id.toLong()
        )
        try {

            context.contentResolver.delete(uri, null, null)

            Toast.makeText(context, "Song deleted", Toast.LENGTH_SHORT).show()

        } catch (e: RecoverableSecurityException) {

            val intentSender =
                e.userAction.actionIntent.intentSender

            if (context is Activity) {
                context.startIntentSenderForResult(
                    intentSender,
                    DELETE_REQUEST_CODE,
                    null,
                    0,
                    0,
                    0
                )
            }

        } catch (e: Exception) {

            Toast.makeText(context, "Unable to delete", Toast.LENGTH_SHORT).show()

        }
    }
}