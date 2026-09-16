package com.example.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackupCryptoTest {

    @Test
    fun `encrypt and decrypt successfully restores original payload`() {
        val originalText = "{\"app\":\"Fikra Video\",\"notes\":\"بيانات سرية ومشروع جديد\"}"
        val password = "SecurePassword@2026".toCharArray()

        val encrypted = BackupCrypto.encrypt(originalText, password)

        assertNotEquals(originalText, encrypted)
        assertTrue(encrypted.contains("."))
        assertEquals(3, encrypted.split(".").size)

        val decrypted = BackupCrypto.decrypt(encrypted, password)
        assertEquals(originalText, decrypted)
    }

    @Test(expected = Exception::class)
    fun `decrypt with wrong password throws exception`() {
        val originalText = "Sensitive Data"
        val password = "CorrectPassword".toCharArray()
        val wrongPassword = "WrongPassword".toCharArray()

        val encrypted = BackupCrypto.encrypt(originalText, password)
        BackupCrypto.decrypt(encrypted, wrongPassword)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `decrypt with malformed payload throws IllegalArgumentException`() {
        val invalidPayload = "not.a.valid.encrypted.payload.format"
        BackupCrypto.decrypt(invalidPayload, "Password".toCharArray())
    }
}
