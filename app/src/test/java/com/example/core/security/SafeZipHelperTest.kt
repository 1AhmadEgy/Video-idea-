package com.example.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.zip.ZipEntry

class SafeZipHelperTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `safeTarget allows valid internal file entries`() {
        val rootDir = tempFolder.newFolder("output")
        val entry = ZipEntry("media/video_1.mp4")

        val target = SafeZipHelper.safeTarget(rootDir, entry)

        assertEquals(File(rootDir, "media/video_1.mp4").canonicalPath, target.canonicalPath)
        assertTrue(target.canonicalPath.startsWith(rootDir.canonicalPath))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `safeTarget rejects zip slip path traversal entries`() {
        val rootDir = tempFolder.newFolder("output")
        val maliciousEntry = ZipEntry("../../etc/passwd")

        SafeZipHelper.safeTarget(rootDir, maliciousEntry)
    }
}
