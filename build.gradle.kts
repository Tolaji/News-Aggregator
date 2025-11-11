//buildscript {
//    dependencies {
//        classpath("com.google.gms:google-services:4.4.0")
//        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
//
//    }
//}

// 1. Plugins Block: All plugins and their versions should be defined here,
//    eliminating the need for the deprecated 'buildscript/dependencies' block.
//    The 'apply false' ensures the plugin is only added to the classpath,
//    and is applied later in the module-level build.gradle.kts files.
plugins {
    // Standard Android/Kotlin plugins (using Version Catalog aliases)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Google Services (Updated to latest stable version 4.4.1)
    // The current commonly accepted stable version is 4.4.1 for Firebase setup.
    id("com.google.gms.google-services") version "4.4.1" apply false

    // Hilt (Dagger Dependency Injection) (Updated to latest stable version 2.51.1)
    id("com.google.dagger.hilt.android") version "2.51.1" apply false

    // KSP (Kotlin Symbol Processing API) (Updated to latest stable version 2.0.0-1.0.21)
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false

    // It is highly recommended to use the latest stable Kotlin version (currently 2.0.0 or higher)
    // in your Version Catalog or explicit declaration for optimal performance.
    // If you are using the Kotlin 2.0.0 compiler, you must use the 2.0.0 plugin version.
    id("org.jetbrains.kotlin.jvm") version "1.9.23" apply false
}
