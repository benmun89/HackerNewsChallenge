# HackerNews Challenge
## About
The Hacker News Challenge is a mobile application built using Kotlin and Jetpack Compose for Android. The app fetches the latest tech news from the Hacker News API and displays it in a clean, modern interface. Users can refresh the news feed, remove individual posts, and Integrate a WebView within the app to display and interact with the build directly from the URL from the items.

This project follows a clean architecture approach and utilizes MVVM (Model-View-ViewModel) with Kotlin Coroutines for managing asynchronous operations. The app also leverages Dagger Hilt for dependency injection and Room for local database storage.


# Features
Fetches and displays news articles from Hacker News.

Supports pull-to-refresh to fetch fresh news.

Allows users to remove articles from the feed.

Uses Kotlin Coroutines for asynchronous data fetching.

Implements Unit Tests and UI Tests for key features, ensuring code reliability.

Includes Mocking and Test Dispatchers for proper coroutine testing in unit tests.

# Architecture
Model: The NewsResponse data model holds the fetched data.

ViewModel: The NewsViewModel handles the logic for loading, refreshing, and removing news.

Repository: The NewsRepository fetches data from both the API and the local database.

Database: Room is used for caching fetched news to avoid unnecessary network requests.

Dependency Injection: Dagger Hilt is used for injecting the required dependencies across the app.

UI: Jetpack Compose is used for the user interface, allowing for a modern, declarative approach to UI development.


# Tech Stack
Kotlin

Jetpack Compose

Kotlin Coroutines

Dagger Hilt (for dependency injection)

Room (for local storage)

Retrofit (for API calls)

JUnit and Mockk (for unit testing)


# Setup
# Prerequisites
Android Studio Bumblebee or newer.

Java 11 or newer.

Internet connection for fetching data from the API.
