package com.example.core.media

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoDownloader @Inject constructor(
    @param:dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    fun download(
        url: String,
        fileName: String = "fikra_video.mp4"
    ): Long {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("فيديو فكرة")
            .setDescription("جارٍ تنزيل الفيديو")
            .setMimeType("video/mp4")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, fileName)

        val manager = context.getSystemService(DownloadManager::class.java)
        return manager.enqueue(request)
    }
}
