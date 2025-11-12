package com.myfirsteverapp.newsaggregator.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object DateUtils {
    private const val TAG = "DateUtils"

    private val iso8601Formatter = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue(): SimpleDateFormat {
            return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        }
    }

    fun parseIso8601ToMillis(rawDate: String?): Long {
        if (rawDate.isNullOrBlank()) return 0L

        return runCatching {
            iso8601Formatter.get().parse(rawDate.trim())?.time ?: 0L
        }.onFailure { throwable ->
            Log.w(TAG, "Failed to parse ISO8601 date: $rawDate", throwable)
        }.getOrElse { 0L }
    }

    fun hoursAgoInMillis(hours: Long): Long {
        return System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)
    }
}

