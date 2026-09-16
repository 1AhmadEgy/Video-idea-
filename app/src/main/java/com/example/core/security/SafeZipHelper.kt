package com.example.core.security

import java.io.File
import java.util.zip.ZipEntry

object SafeZipHelper {

    /**
     * Validates and returns the target file for a zip entry, preventing Path Traversal (Zip Slip).
     */
    fun safeTarget(
        outputDir: File,
        entry: ZipEntry
    ): File {
        val target = File(outputDir, entry.name)
        val rootPath = outputDir.canonicalFile.toPath()
        val targetPath = target.canonicalFile.toPath()

        require(targetPath.startsWith(rootPath)) {
            "Invalid archive entry path: ${entry.name}"
        }

        return target
    }
}
