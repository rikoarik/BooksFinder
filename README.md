# BookFinder - Android App

A modern Android application built with Kotlin, Jetpack Compose, and Material 3 design that allows users to search for books using the Open Library API and manage their favorite books. The app features a beautiful, modern UI inspired by contemporary design trends with support for dark/light themes.

## ✨ Features

- **📚 Initial Book Display**: Automatically displays popular books when first opened without requiring search
- **🔍 Smart Search**: Search for books by title or author using the Open Library API with real-time results
- **🔄 Smart Sorting**: Sort books by relevance, newest, oldest, random, or key - works independently of search
- **📖 Manual Paging**: Efficient manual pagination with auto-load more when scrolling (no Paging 3 dependency)
- **❤️ Favorites Management**: Save, view, and manage favorite books with persistent local storage
- **📖 Book Details**: Comprehensive book information including cover, description, publisher, ISBN, and subjects
- **🎨 Modern UI**: Clean, Material 3 design with Jetpack Compose and custom Poppins typography
- **🌙 Dark/Light Theme**: Toggle between dark and light themes with persistent preferences
- **📱 Offline Support**: View your favorite books even without internet connection
- **🎯 Responsive Design**: Optimized for different screen sizes and orientations
- **⚙️ Settings**: Easy access to theme preferences
- **🚀 Performance Optimized**: Fast loading with optimized page size and efficient data management

## 🏗️ Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern with clean separation of concerns:

- **Data Layer**: Repository pattern with Room database, Retrofit API, and DataStore preferences
- **Domain Layer**: Business logic and data models with Resource sealed class for state management
- **Presentation Layer**: Jetpack Compose UI with ViewModels and StateFlow
- **State Management**: Resource sealed class for loading, success, and error states
- **Preferences**: DataStore for theme settings persistence
- **Manual Paging**: Custom pagination implementation without external libraries

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room with Kotlin Coroutines and Flow
- **Networking**: Retrofit with OkHttp and Gson
- **Image Loading**: Coil for efficient image loading and caching
- **Navigation**: Navigation Compose for screen navigation
- **State Management**: StateFlow and Resource sealed class
- **Design System**: Material 3 with custom Poppins typography
- **Preferences**: DataStore for user settings persistence
- **Code Generation**: KSP for Room annotation processing
- **Pagination**: Custom manual implementation (no Paging 3 dependency)
- **Build System**: Gradle with Kotlin DSL

## 📁 Project Structure

```
app/src/main/java/com/app/bookfinder/
├── data/
│   ├── local/          # Room database, DAO, and entities
│   ├── remote/         # Retrofit API, network models, and API responses
│   ├── repository/     # Repository implementation and data sources
│   ├── model/          # Data models, Resource class, and API responses
│   └── preferences/    # User preferences and DataStore implementation
├── ui/
│   ├── components/     # Reusable UI components (BookCard, SearchBar, FilterSection)
│   ├── screens/        # Screen composables (Home, Detail, Settings, Favorites)
│   ├── theme/          # Material 3 theme, colors, and typography
│   ├── navigation/     # Navigation setup and route definitions
│   └── viewmodel/      # ViewModels for state management and business logic
├── MainActivity.kt     # Main activity entry point
└── BookFinderApplication.kt  # Application class
```

## 🚀 Setup Instructions

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 26+ (API level 26)
- Kotlin 2.0.21+
- JDK 11 or later

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/rikoarik/BooksFinder.git
   cd BookFinder
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the BookFinder folder and open it

3. **Sync Gradle files**
   - Wait for Gradle sync to complete
   - Resolve any dependency issues if they arise

4. **Font Setup (Required)**
   Download Poppins fonts from [Google Fonts](https://fonts.google.com/specimen/Poppins) and place them in `app/src/main/res/font/`:
   - `font_poppins_light.ttf`
   - `font_poppins_regular.ttf`
   - `font_poppins_medium.ttf`
   - `font_poppins_semibold.ttf`
   - `font_poppins_bold.ttf`

5. **Build and Run**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift+F10

### Font Setup Alternative

If you prefer not to download the Poppins fonts, you can modify `app/src/main/java/com/app/bookfinder/ui/theme/Type.kt` to use the system default font by replacing `Poppins` with `FontFamily.Default`.

## 🎨 Theme System

The app features a sophisticated theme system with Material 3:

- **Light Theme**: Clean, bright interface with subtle shadows and gradients
- **Dark Theme**: Modern dark interface with proper contrast and readability
- **Persistent**: Theme preference is saved and restored on app restart
- **Dynamic**: Smooth transitions between themes
- **Custom Colors**: Tailored color scheme for optimal user experience

## 🔌 API Integration

The app integrates with the **Open Library API** for comprehensive book data:

- **Search Endpoint**: `/search.json` - Search for books by query with pagination
- **Work Details**: `/works/{workId}.json` - Get detailed book information
- **Cover Images**: `https://covers.openlibrary.org/b/id/{coverId}-L.jpg` - High-quality book covers
- **Multiple Fields**: Title, author, publisher, ISBN, subjects, and more

## 💾 Database Schema

The Room database provides local storage for:

- **Book Information**: Title, author, publisher, ISBN, cover ID, subjects
- **Favorite Status**: Persistent favorite book management
- **Local Data**: Offline access to favorite books
- **Efficient Queries**: Optimized database operations with Room

## 🎯 Key Components

### SearchBar
Modern search component with Material 3 design:
- Real-time search as you type
- Clear search functionality
- Beautiful rounded design with elevation
- Loading states and error handling

### BookCard
Beautiful book card displaying comprehensive information:
- High-quality cover images with Coil
- Title, author, and publication year
- Favorite toggle with heart icon
- Modern card design with gradients

### FilterSection
Smart sorting options that work independently of search:
- **Relevansi**: Default relevance-based sorting
- **Edisi Terbaru**: Newest editions first
- **Edisi Terlama**: Oldest editions first
- **Acak**: Random book ordering
- **Berdasarkan Key**: Key-based sorting

### Settings Screen
Elegant settings interface for user preferences:
- Theme switching (Light/Dark/System)
- Modern card-based design
- Persistent preference storage

### Resource Class
Sealed class managing UI states:
- `Loading`: Shows progress indicator with modern card design
- `Success<T>`: Displays data with proper error handling
- `Error`: Shows error message with retry button

## 🚀 Performance Features

### Manual Paging System
- **Page Size**: 20 books per page for optimal performance
- **Auto-Load**: Automatically loads more books when scrolling near the end
- **Loading States**: Clear loading indicators during pagination
- **Efficient**: No external Paging 3 library dependency
- **Memory Optimized**: Efficient data management and state persistence

### Smart Loading
- **Initial Load**: Popular books displayed immediately on app launch
- **Sort-Based Loading**: Books load based on selected sort option without search
- **Search Integration**: Search results with manual pagination
- **Caching**: Efficient data management and state persistence
- **Background Processing**: Non-blocking UI during data loading

## 🎨 Design Inspiration

The app's modern design is inspired by contemporary UI/UX trends:

- **Rounded Corners**: Modern card designs with subtle shadows
- **Gradient Backgrounds**: Beautiful color transitions and modern schemes
- **Typography**: Clean Poppins font with proper hierarchy
- **Animations**: Smooth transitions and micro-interactions
- **Layout**: Card-based layouts with proper spacing and alignment
- **Material 3**: Latest Material Design principles and components

## 🔧 Configuration

### Theme Switching
Users can toggle between light and dark themes through the Settings screen:
- **Light Theme**: Bright, clean interface
- **Dark Theme**: Modern dark interface
- **System Theme**: Follows device theme preference
- **Persistent**: Theme choice is saved and restored

### Sort Options
Sort books independently of search:
- Click any sort option to immediately load books with that criteria
- No search query required for sorting
- Seamless integration with search when query is present
- Real-time sorting with loading indicators

## 📱 Screenshots

The app features several key screens with modern design:

- **Home Screen**: Modern search interface with tab navigation and initial book display
- **Search Results**: Beautiful book cards with cover images and manual pagination
- **Book Details**: Comprehensive book information display with cover images
- **Favorites**: Personal book collection with offline access
- **Settings**: Theme preferences with modern interface

## 🔍 Search & Navigation Features

### Smart Search
- **Instant Display**: Popular books shown immediately without search
- **Real-time Search**: Search as you type with instant results
- **Clear Search**: Easy return to popular books display
- **Sort Integration**: Search results respect selected sort options
- **Pagination**: Manual pagination for search results

### Navigation
- **Smooth Transitions**: Seamless navigation between screens
- **Book Details**: Full book information with cover images
- **Back Navigation**: Intuitive back button functionality
- **Deep Linking**: Direct navigation to book details
- **Tab Navigation**: Easy switching between Search and Favorites

## 🧪 Testing

The app includes comprehensive testing:

- **Unit Tests**: ViewModel and Repository testing
- **UI Tests**: Compose UI testing with Espresso
- **Integration Tests**: End-to-end functionality testing
- **Test Coverage**: Comprehensive test coverage for critical components

## 🚀 Build Variants

The app supports multiple build configurations:

- **Debug**: Development build with logging and debugging
- **Release**: Production build with optimizations
- **Custom**: Configurable build variants for different environments

## 🤝 Contributing

We welcome contributions to improve BookFinder:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Make your changes** with proper code formatting
4. **Add tests** if applicable for new functionality
5. **Update documentation** to reflect changes
6. **Submit a pull request** with detailed description

### Contribution Guidelines

- Follow Kotlin coding conventions
- Use meaningful commit messages
- Include tests for new features
- Update documentation as needed
- Follow Material Design principles

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Open Library](https://openlibrary.org/) for providing the comprehensive book data API
- [Google Fonts](https://fonts.google.com/) for the beautiful Poppins font family
- [Material Design](https://material.io/) for design guidelines and principles
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern Android UI development
- [Android Developers](https://developer.android.com/) for platform guidance and best practices
- Modern UI/UX design trends and inspiration from the design community
- Contributors and users who provide feedback and suggestions

## 📞 Support

If you encounter any issues or have questions:

- **GitHub Issues**: Report bugs and request features
- **Documentation**: Check this README for setup and usage
- **Community**: Join discussions and share experiences

---

**BookFinder** - Discover, explore, and manage your favorite books with style! 📚✨
