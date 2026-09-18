package com.example.core.media

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoShareManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun shareUrl(videoUrl: String) {
        val file = when {
            videoUrl.startsWith("file://") -> File(videoUrl.removePrefix("file://"))
            videoUrl.startsWith("/") -> File(videoUrl)
            else -> null
        }

        val sendIntent = if (file != null && file.exists()) {
            FileShareHelper.createShareIntent(context, file, "video/mp4").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, videoUrl)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        val chooser = Intent.createChooser(sendIntent, "مشاركة الفيديو").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooser)
    }
}
