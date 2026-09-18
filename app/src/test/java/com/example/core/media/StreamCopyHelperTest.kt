package com.example.core.media

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

class StreamCopyHelperTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `copyStream copies all bytes accurately`() {
        val testData = "Test stream content for streaming media safely".toByteArray(Charsets.UTF_8)
        val input = ByteArrayInputStream(testData)
        val output = ByteArrayOutputStream()

        val copiedBytes = StreamCopyHelper.copyStream(input, output)

        assertEquals(testData.size.toLong(), copiedBytes)
        assertArrayEquals(testData, output.toByteArray())
    }

    @Test
    fun `copyFile accurately copies file without data loss`() {
        val src = tempFolder.newFile("source.bin")
        val dst = tempFolder.newFile("dest.bin")
        val testContent = "Binary media simulation data".toByteArray()
        src.writeBytes(testContent)

        val copied = StreamCopyHelper.copyFile(src, dst)

        assertEquals(testContent.size.toLong(), copied)
        assertArrayEquals(testContent, dst.readBytes())
    }
}
