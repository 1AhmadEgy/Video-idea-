package com.example.core.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCrypto {

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_ALGORITHM = "AES"
    private const val ITERATIONS = 120_000

    fun encrypt(
        plainText: String,
        password: CharArray
    ): String {
        val salt = ByteArray(16).also {
            SecureRandom().nextBytes(it)
        }

        val iv = ByteArray(12).also {
            SecureRandom().nextBytes(it)
        }

        val key = deriveKey(password, salt)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            key,
            GCMParameterSpec(128, iv)
        )

        val ciphertext = cipher.doFinal(
            plainText.toByteArray(Charsets.UTF_8)
        )

        return listOf(
            salt,
            iv,
            ciphertext
        ).joinToString(".") {
            Base64.encodeToString(it, Base64.NO_WRAP)
        }
    }

    fun decrypt(
        payload: String,
        password: CharArray
    ): String {
        val parts = payload.split(".")
        require(parts.size == 3) { "Invalid encrypted payload format" }

        val salt = Base64.decode(parts[0], Base64.NO_WRAP)
        val iv = Base64.decode(parts[1], Base64.NO_WRAP)
        val ciphertext = Base64.decode(parts[2], Base64.NO_WRAP)

        val key = deriveKey(password, salt)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            key,
            GCMParameterSpec(128, iv)
        )

        return cipher.doFinal(ciphertext).toString(Charsets.UTF_8)
    }

    private fun deriveKey(
        password: CharArray,
        salt: ByteArray
    ): SecretKey {
        val spec = PBEKeySpec(
            password,
            salt,
            ITERATIONS,
            256
        )

        return try {
            SecretKeySpec(
                SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .encoded,
                KEY_ALGORITHM
            )
        } finally {
            spec.clearPassword()
        }
    }
}
