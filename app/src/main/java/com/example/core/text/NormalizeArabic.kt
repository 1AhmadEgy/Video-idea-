package com.example.core.text

object ArabicTextNormalizer {
    /**
     * Normalizes Arabic text by removing diacritics (Tashkeel) and standardizing letters (Alef, Yaa, Haa).
     */
    fun normalizeArabic(input: String): String {
        if (input.isBlank()) return ""

        var result = input
            // Remove Arabic diacritics
            .replace(Regex("[\\u064B-\\u065F\\u0670]"), "")
            // Normalize Alef variants
            .replace(Regex("[\\u0622\\u0623\\u0625]"), "\u0627")
            // Normalize Yaa / Alef Maksura
            .replace("\u0649", "\u064A")
            // Normalize Taa Marbuta to Haa
            .replace("\u0629", "\u0647")
            .trim()

        return result
    }
}
