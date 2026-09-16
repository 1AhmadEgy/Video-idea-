package com.example.core.media

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoShareManager @Inject constructor(
    private val context: Context
) {
    fun shareUrl(videoUrl: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, videoUrl)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(sendIntent, "مشاركة الفيديو").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        ContextCompat.startActivity(context, chooser, null)
    }
}
