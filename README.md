# BookFinder - Android App

A modern Android application built with Kotlin, Jetpack Compose, and Material 3 design that allows users to search for books using the Open Library API and manage their favorite books. The app features a beautiful, modern UI inspired by contemporary design trends with support for dark/light themes and multiple languages.

## ✨ Features

- **🔍 Book Search**: Search for books by title or author using the Open Library API
- **🎨 Modern UI**: Clean, Material 3 design with Jetpack Compose inspired by modern design trends
- **🌙 Dark/Light Theme**: Toggle between dark and light themes with persistent preferences
- **🌍 Multi-Language**: Support for English and Indonesian languages
- **📚 Book Details**: Comprehensive book information including cover, description, publisher, ISBN, and subjects
- **❤️ Favorites**: Save and manage your favorite books locally using Room database
- **📱 Offline Support**: View your favorite books even without internet connection
- **🎯 Responsive Design**: Optimized for different screen sizes and orientations
- **⚙️ Settings**: Easy access to theme and language preferences

## 🏗️ Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern:

- **Data Layer**: Repository pattern with Room database and Retrofit API
- **Domain Layer**: Use cases and business logic
- **Presentation Layer**: Jetpack Compose UI with ViewModels
- **State Management**: Resource sealed class for loading, success, and error states
- **Preferences**: DataStore for theme and language settings

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Database**: Room with Kotlin Coroutines
- **Networking**: Retrofit with OkHttp
- **Image Loading**: Coil
- **Navigation**: Navigation Compose
- **State Management**: StateFlow and Resource sealed class
- **Design System**: Material 3 with custom Poppins typography
- **Preferences**: DataStore for user settings
- **Code Generation**: KSP for Room

## 📁 Project Structure

```
app/src/main/java/com/app/bookfinder/
├── data/
│   ├── local/          # Room database and DAO
│   ├── remote/         # Retrofit API and network models
│   ├── repository/     # Repository implementation
│   ├── model/          # Data models and Resource class
│   └── preferences/    # User preferences and DataStore
├── ui/
│   ├── components/     # Reusable UI components
│   ├── screens/        # Screen composables
│   ├── theme/          # Material 3 theme and typography
│   ├── navigation/     # Navigation setup
│   └── viewmodel/      # ViewModels for state management
└── BookFinderApplication.kt
```

## 🚀 Setup Instructions

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 26+ (API level 26)
- Kotlin 2.0.21+

### Installation

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Download Poppins fonts from [Google Fonts](https://fonts.google.com/specimen/Poppins)
5. Place the font files in `app/src/main/res/font/` directory:
   - `poppins_light.ttf`
   - `poppins_regular.ttf`
   - `poppins_medium.ttf`
   - `poppins_semibold.ttf`
   - `poppins_bold.ttf`
6. Build and run the application

### Font Setup Alternative

If you prefer not to download the Poppins fonts, you can modify `app/src/main/java/com/app/bookfinder/ui/theme/Type.kt` to use the system default font by replacing `Poppins` with `FontFamily.Default`.

## 🌐 Localization

The app supports multiple languages:
- **English** (default)
- **Indonesian** (Bahasa Indonesia)

Users can switch languages through the Settings screen, and all text will be automatically updated.

## 🎨 Theme System

The app features a sophisticated theme system:
- **Light Theme**: Clean, bright interface with subtle shadows and gradients
- **Dark Theme**: Modern dark interface with proper contrast and readability
- **Persistent**: Theme preference is saved and restored on app restart
- **Dynamic**: Smooth transitions between themes

## 🔌 API Integration

The app integrates with the **Open Library API**:

- **Search Endpoint**: `/search.json` - Search for books by query
- **Work Details**: `/works/{workId}.json` - Get detailed book information
- **Cover Images**: `https://covers.openlibrary.org/b/id/{coverId}-L.jpg`

## 💾 Database Schema

The Room database stores:
- Book information (title, author, publisher, ISBN, etc.)
- Favorite status
- Local search history

## 🎯 Key Components

### SearchBar
Modern search component with Material 3 design, supporting real-time search and clear functionality.

### BookCard
Beautiful book card displaying cover image, title, author, year, and publisher with modern favorite toggle.

### Settings Screen
Elegant settings interface for theme and language preferences with modern card-based design.

### Resource Class
Sealed class managing UI states:
- `Loading`: Shows progress indicator with modern card design
- `Success<T>`: Displays data
- `Error`: Shows error message with retry button

## 🎨 Design Inspiration

The app's modern design is inspired by contemporary UI/UX trends, featuring:
- Rounded corners and subtle shadows
- Gradient backgrounds and modern color schemes
- Clean typography with proper hierarchy
- Smooth animations and transitions
- Card-based layouts with proper spacing

## 🔧 Configuration

### Theme Switching
Users can toggle between light and dark themes through the Settings screen. The preference is automatically saved and restored.

### Language Switching
Support for English and Indonesian languages with easy switching through the Settings screen.

## 📱 Screenshots

The app features several key screens:
- **Home Screen**: Modern search interface with tab navigation
- **Search Results**: Beautiful book cards with cover images
- **Book Details**: Comprehensive book information display
- **Favorites**: Personal book collection
- **Settings**: Theme and language preferences

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- [Open Library](https://openlibrary.org/) for providing the book data API
- [Google Fonts](https://fonts.google.com/) for the Poppins font family
- [Material Design](https://material.io/) for design guidelines
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern Android UI development
- Modern UI/UX design trends and inspiration from the design community
