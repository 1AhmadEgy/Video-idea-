package com.example.core.time

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun formatProjectDate(
    timestamp: Long,
    now: Long = System.currentTimeMillis()
): String {
    if (timestamp <= 0L) {
        return "بدون تاريخ"
    }

    val difference = now - timestamp

    if (difference < 0L) {
        return formatAbsoluteDate(timestamp)
    }

    val minutes = TimeUnit.MILLISECONDS.toMinutes(difference)
    val days = TimeUnit.MILLISECONDS.toDays(difference)

    return when {
        minutes < 1L -> "الآن"
        minutes < 60L -> "منذ $minutes دقيقة"
        isSameDay(timestamp, now) -> "اليوم، ${formatTime(timestamp)}"
        isYesterday(timestamp, now) -> "أمس، ${formatTime(timestamp)}"
        days < 7L -> "منذ $days أيام"
        else -> formatAbsoluteDate(timestamp)
    }
}

private fun isSameDay(first: Long, second: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = first }
    val cal2 = Calendar.getInstance().apply { timeInMillis = second }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isYesterday(first: Long, now: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = first }
    val cal2 = Calendar.getInstance().apply {
        timeInMillis = now
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("hh:mm a", Locale.forLanguageTag("ar")).format(Date(timestamp))
}

private fun formatAbsoluteDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("ar")).format(Date(timestamp))
}
