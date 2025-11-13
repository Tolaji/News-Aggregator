# Weflut Live News Agrregator
# Overview

Being a software developer passionate about modern mobile development, Weflut Live was developed to deepen my understanding and expertise in cloud-native Android applications. This project represents my journey into mastering Kotlin coroutines, reactive programming with Flow, and real-time data synchronization using Firebase Firestore. The app serves as a comprehensive news aggregation platform that demonstrates how to build scalable, maintainable mobile applications with cutting-edge technologies.

Weflut Live is a feature-rich Android news aggregator that delivers personalized news experiences. Users can browse news across multiple categories, search for specific topics, bookmark articles for later reading, and enjoy real-time synchronization across all their devices. The app intelligently filters content by freshness and provides a seamless reading experience with modern Material Design 3 principles.

**How to Use the App:**
1. **Authentication**: Sign up or log in with email/password to access personalized features
2. **Browse News**: Swipe through category chips to explore different news topics
3. **Search**: Use the search functionality with intelligent debouncing for efficient queries
4. **Bookmark**: Tap the bookmark icon on any article to save it for offline reading
5. **Sync**: All bookmarks automatically sync across devices via Firebase
6. **Refresh**: Pull down on the home screen to fetch latest news updates

My purpose for developing Weflut Live was to create a production-ready application that showcases modern Android development best practices. I wanted to demonstrate proficiency in reactive architecture patterns, cloud database integration, and declarative UI development. This project serves as a portfolio piece that illustrates my ability to handle complex state management, real-time data flows, and professional-grade app architecture.

[Software Demo Video](http://youtube.link.goes.here)

# Development Environment

I developed Weflut Live using **Android Studio Hedgehog** as the primary IDE, leveraging its robust Kotlin support and advanced Compose tooling. The project was built with a focus on modern Android development practices and cloud integration.

**Programming Language & Libraries:**
- **Kotlin**: 100% Kotlin codebase utilizing modern language features including coroutines, Flow, sealed classes, and extension functions
- **Jetpack Compose**: Declarative UI framework for building reactive, maintainable interfaces
- **Firebase Platform**:
    - **Firestore**: Cloud database for real-time data synchronization
    - **Authentication**: Secure user management with email/password
- **Retrofit + OkHttp**: Type-safe HTTP client for API communication with NewsAPI
- **Hilt**: Dependency injection framework for managing app dependencies
- **Coil**: Efficient image loading library for Compose
- **Navigation Compose**: Type-safe navigation with argument passing

The development environment was configured with **Gradle Kotlin DSL** for build configuration, ensuring type-safe build scripts and better IDE support. I utilized the **Android Emulator** with various API levels for testing across different device configurations.

# Useful Websites

* [Android Developer Documentation](https://developer.android.com) - Comprehensive guides on Jetpack Compose, coroutines, and architecture patterns
* [Firebase Documentation](https://firebase.google.com/docs) - Detailed Firestore security rules and real-time database implementation
* [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html) - In-depth understanding of structured concurrency
* [Material Design 3](https://m3.material.io/) - Design system principles and component specifications
* [NewsAPI Documentation](https://newsapi.org/docs) - REST API integration for news data sourcing

# Future Work

* **Push Notifications**: Implement Firebase Cloud Messaging to deliver breaking news alerts and personalized notifications based on user preferences
* **Offline-First Strategy**: Integrate Room database for robust offline caching and implement sync conflict resolution strategies
* **Personalized Recommendations**: Develop machine learning features using ML Kit to provide tailored news suggestions based on reading history
* **Social Features**: Add ability to share articles, create reading lists, and follow other users' reading activities
* **Performance Optimization**: Implement pagination for large article lists and optimize image loading with placeholder strategies
* **Accessibility Improvements**: Enhance screen reader support, focus management, and color contrast ratios for better accessibility compliance
* **Testing Suite**: Expand unit tests for ViewModels, integration tests for repository layer, and UI tests for critical user flows
