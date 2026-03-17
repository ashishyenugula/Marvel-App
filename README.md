# Marvel Characters App

An Android application built with **Kotlin** and **Jetpack Compose** that allows users to browse the Marvel library of characters using the Marvel API.

## Architecture

This project follows **Clean Architecture** with **MVVM** pattern:

```
com.marvel.app/
├── data/                    # Data layer
│   ├── api/                 # Retrofit API interface + auth interceptor
│   ├── model/               # DTOs and mappers
│   ├── paging/              # Paging 3 data source
│   └── repository/          # Repository implementation
├── domain/                  # Domain layer
│   ├── model/               # Domain models
│   ├── repository/          # Repository interface
│   └── usecase/             # Use cases
├── presentation/            # Presentation layer
│   ├── navigation/          # Navigation graph
│   ├── list/                # Character list (ViewModel + Screen)
│   ├── detail/              # Character detail (ViewModel + Screen)
│   ├── theme/               # Material 3 theme
│   └── components/          # Shared composables
├── di/                      # Hilt dependency injection
├── MarvelApp.kt             # Application class
└── MainActivity.kt          # Entry point
```

## Features

- **Character List** with infinite scroll pagination (Paging 3)
- **Character Detail** with hero image, description, and expandable resource sections
- **Lazy-loaded images** for comics, series, stories, and events fetched from resourceURI
- **Smooth animations** — slide transitions, expand/collapse, fade effects
- **Error handling** with retry functionality
- **Dark theme** support

## Tech Stack

| Library | Purpose |
|---------|---------|
| Jetpack Compose | Declarative UI |
| Hilt | Dependency Injection |
| Retrofit + OkHttp | Networking |
| Paging 3 | Infinite scroll |
| Coil | Image loading |
| Navigation Compose | Screen navigation |
| Material 3 | Design system |

## Setup

1. Get Marvel API keys from [developer.marvel.com](https://developer.marvel.com)
2. Open `app/build.gradle` and replace:
   ```groovy
   buildConfigField "String", "MARVEL_PUBLIC_KEY", '"YOUR_PUBLIC_KEY"'
   buildConfigField "String", "MARVEL_PRIVATE_KEY", '"YOUR_PRIVATE_KEY"'
   ```
3. Build and run the project

## Design Decisions

- **Clean Architecture** separates concerns — data layer doesn't leak into UI
- **Use Cases** encapsulate business logic and map DTOs to domain models
- **PagingSource** handles Marvel API's offset-based pagination
- **Auth Interceptor** automatically appends API credentials + MD5 hash to every request
- **StateFlow** drives reactive UI updates from ViewModels
- **Sections hidden** when no data is returned from the API, as specified
- **LaunchedEffect** triggers lazy image loading when resource cards become visible
