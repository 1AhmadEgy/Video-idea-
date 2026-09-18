package com.example.core.text

import org.junit.Assert.assertEquals
import org.junit.Test

class NormalizeArabicTest {

    @Test
    fun `normalize removes diacritics`() {
        val input = "فِكْرَةُ فِيدْيُو"
        val expected = "فكره فيديو"
        assertEquals(expected, ArabicTextNormalizer.normalizeArabic(input))
    }

    @Test
    fun `normalize standardizes alef forms`() {
        val input = "أحمد إبراهيم آمنة"
        val expected = "احمد ابراهيم امنه"
        assertEquals(expected, ArabicTextNormalizer.normalizeArabic(input))
    }

    @Test
    fun `normalize handles empty string`() {
        assertEquals("", ArabicTextNormalizer.normalizeArabic(""))
        assertEquals("", ArabicTextNormalizer.normalizeArabic("   "))
    }
}
