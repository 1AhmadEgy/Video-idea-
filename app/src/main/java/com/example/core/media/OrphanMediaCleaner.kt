package com.example.core.media

import android.content.Context
import com.example.core.database.dao.AssetDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrphanMediaCleaner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val assetDao: AssetDao
) {
    suspend fun clean() = withContext(Dispatchers.IO) {
        val assets: List<com.example.core.database.entity.AssetEntity> =
            runCatching { assetDao.getAllAssets() }.getOrDefault(emptyList())

        val referencedPaths: Set<String> = assets.map { entity ->
            val uri = entity.storageUri
            if (uri.startsWith("file://")) {
                uri.removePrefix("file://")
            } else {
                uri
            }
        }.map { pathString ->
            runCatching { File(pathString).canonicalPath }.getOrDefault(pathString)
        }.toSet()

        val rootMediaDir = File(context.filesDir, "media")
        if (rootMediaDir.exists()) {
            rootMediaDir.walkTopDown()
                .filter { it.isFile }
                .forEach { file ->
                    val path = runCatching { file.canonicalPath }.getOrDefault(file.absolutePath)
                    if (path !in referencedPaths) {
                        file.delete()
                    }
                }
        }

        val shareCacheDir = File(context.cacheDir, "share")
        if (shareCacheDir.exists()) {
            val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
            shareCacheDir.walkTopDown()
                .filter { it.isFile && it.lastModified() < oneDayAgo }
                .forEach { file ->
                    file.delete()
                }
        }
    }
}
