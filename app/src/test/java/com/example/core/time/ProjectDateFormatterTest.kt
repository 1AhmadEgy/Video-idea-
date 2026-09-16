package com.example.core.time

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ProjectDateFormatterTest {

    @Test
    fun `returns fallback when timestamp is zero or negative`() {
        assertEquals("بدون تاريخ", formatProjectDate(0L))
        assertEquals("بدون تاريخ", formatProjectDate(-100L))
    }

    @Test
    fun `returns now when timestamp is within one minute`() {
        val now = 1700000000000L
        val timestamp = now - 30_000L // 30 seconds ago
        assertEquals("الآن", formatProjectDate(timestamp, now))
    }

    @Test
    fun `returns minutes when timestamp is within one hour`() {
        val now = 1700000000000L
        val timestamp = now - 15 * 60 * 1000L // 15 mins ago
        assertEquals("منذ 15 دقيقة", formatProjectDate(timestamp, now))
    }

    @Test
    fun `returns today when timestamp is on same day`() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 12, 14, 30, 0)
        }
        val now = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 10)
        val timestamp = calendar.timeInMillis

        val result = formatProjectDate(timestamp, now)
        assertTrue(result.startsWith("اليوم،"))
    }

    @Test
    fun `returns yesterday when timestamp is yesterday`() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 12, 14, 30, 0)
        }
        val now = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val timestamp = calendar.timeInMillis

        val result = formatProjectDate(timestamp, now)
        assertTrue(result.startsWith("أمس،"))
    }

    @Test
    fun `returns days ago when timestamp is within week`() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 12, 14, 30, 0)
        }
        val now = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, -3)
        val timestamp = calendar.timeInMillis

        val result = formatProjectDate(timestamp, now)
        assertEquals("منذ 3 أيام", result)
    }
}
