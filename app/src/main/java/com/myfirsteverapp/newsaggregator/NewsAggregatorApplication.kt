package com.myfirsteverapp.newsaggregator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * This is the custom Application class for the News Aggregator app.
 *
 * The 'android:name' attribute in AndroidManifest.xml links the entire application process
 * to this specific class.
 *
 * The @HiltAndroidApp annotation is essential for Dagger Hilt to generate
 * the necessary components for dependency injection at the application level.
 */
@HiltAndroidApp
class NewsAggregatorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // This is the ideal place to initialize any app-wide, singleton resources,
        // such as global configuration, logging, or non-Hilt-managed SDKs.
    }
}
