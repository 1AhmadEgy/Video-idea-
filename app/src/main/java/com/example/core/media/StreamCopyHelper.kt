package com.example.core.media

import java.io.File
import java.io.InputStream
import java.io.OutputStream

object StreamCopyHelper {
    private const val BUFFER_SIZE = 8192

    /**
     * Safely copies an input stream to an output stream without loading the entire payload into RAM.
     */
    fun copyStream(input: InputStream, output: OutputStream): Long {
        var totalBytesCopied = 0L
        val buffer = ByteArray(BUFFER_SIZE)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
            totalBytesCopied += bytesRead
        }
        output.flush()
        return totalBytesCopied
    }

    /**
     * Safely copies source file to destination using bounded streaming buffers.
     */
    fun copyFile(source: File, destination: File): Long {
        return source.inputStream().use { input ->
            destination.outputStream().use { output ->
                copyStream(input, output)
            }
        }
    }
}
