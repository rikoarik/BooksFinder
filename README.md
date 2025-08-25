# 📚 BookFinder - Android Book Search App

A modern Android application built with Jetpack Compose for searching and discovering books using the OpenLibrary API. Features a collapsible search interface, comprehensive book details, and offline favorites management.

## ✨ Features

### 🔍 **Smart Search Interface**
- **Collapsible Search Bar**: Search bar yang bisa di-tutup dan di-buka dengan animasi smooth
- **Horizontal Expand Animation**: Search bar expand horizontal dari kiri ke kanan saat di-klik
- **Auto-focus Management**: Otomatis focus ke input field saat search bar expand
- **Smart State Management**: Auto-clear search query saat di-collapse

### 📱 **Modern UI/UX**
- **Material Design 3**: Menggunakan Material You design system
- **Smooth Animations**: 300ms duration dengan tween easing
- **Responsive Layout**: Adaptif untuk berbagai ukuran layar
- **Dark/Light Theme**: Support untuk tema gelap dan terang

### 🗂️ **Tab Navigation**
- **Book List Tab**: Daftar buku dengan infinite scroll dan load more
- **Favorites Tab**: Manajemen buku favorit dengan offline storage
- **Smart Tab Logic**: 2 tab yang jelas tanpa redundansi

### ⚙️ **Settings & Configuration**
- **Settings Button**: Icon settings (⚙️) di top bar untuk akses cepat
- **Theme Toggle**: Switch antara light dan dark mode
- **User Preferences**: Persistent settings dengan DataStore

### 📖 **Book Management**
- **Comprehensive Search**: Search berdasarkan judul, author, subject
- **Book Details**: Informasi lengkap buku dengan cover image
- **Favorites System**: Save/unsave buku ke favorites
- **Offline Storage**: Data tersimpan lokal dengan Room database

### 🔧 **Advanced Features**
- **Filter & Sort**: Sort berdasarkan relevansi, edisi terbaru, klasik, acak
- **Infinite Scroll**: Load more books dengan pagination
- **Error Handling**: Proper error states dan retry functionality
- **Loading States**: Smooth loading indicators

## 🏗️ Architecture

### **MVVM Pattern**
- **ViewModel**: `BookViewModel`, `BookDetailViewModel`
- **Repository**: `BookRepository` dengan local dan remote data sources
- **UI State**: Reactive UI dengan StateFlow dan Compose

### **Data Layer**
- **Local**: Room database dengan offline caching
- **Remote**: OpenLibrary API dengan Retrofit
- **Caching**: In-memory cache dengan TTL untuk API responses

### **Navigation**
- **Compose Navigation**: Single Activity dengan multiple screens
- **Deep Linking**: Support untuk direct navigation ke book details
- **Back Stack**: Proper navigation history management

## 🚀 Getting Started

### **Prerequisites**
- Android Studio Hedgehog | 2023.1.1 or later
- Android SDK 26 (API level 26) or higher
- Kotlin 1.9.0 or later

### **Installation**
1. Clone repository:
   ```bash
   git clone https://github.com/yourusername/BookFinder.git
   cd BookFinder
   ```

2. Open project in Android Studio

3. Sync Gradle files

4. Build and run on device/emulator

### **Build Variants**
- **Dev Debug**: Development version dengan logging enabled
- **Dev Release**: Development release version
- **Prod Debug**: Production debug version
- **Prod Release**: Production release version

## 🔑 Keystore Setup

### **Generate Keystores**
```bash
# Release keystore
keytool -genkey -v -keystore app/keystore/release.keystore -alias release -keyalg RSA -keysize 2048 -validity 10000

# Debug keystore  
keytool -genkey -v -keystore app/keystore/debug.keystore -alias debug -keyalg RSA -keysize 2048 -validity 10000
```

### **Keystore Location**
- **Release**: `app/keystore/release.keystore`
- **Debug**: `app/keystore/debug.keystore`

### **Build Configuration**
```kotlin
signingConfigs {
    release {
        storeFile file("keystore/release.keystore")
        storePassword "your_store_password"
        keyAlias "release"
        keyPassword "your_key_password"
    }
}
```

## 📦 Building APKs

### **Development Build**
```bash
./gradlew assembleDevDebug    # Debug APK
./gradlew assembleDevRelease  # Release APK
```

### **Production Build**
```bash
./gradlew assembleProdDebug   # Debug APK
./gradlew assembleProdRelease # Release APK
```

### **APK Naming Convention**
- **Dev**: `app-dev-debug.apk`, `app-dev-release.apk`
- **Prod**: `app-prod-debug.apk`, `app-prod-release.apk`

## 🛡️ ProGuard Configuration

### **Code Obfuscation**
- **Release builds**: ProGuard enabled untuk code shrinking
- **Debug builds**: ProGuard disabled untuk development
- **Custom rules**: Comprehensive rules untuk Compose, Retrofit, Room

### **ProGuard Rules**
```proguard
# Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Retrofit & OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
```

## 🌐 API Integration

### **OpenLibrary API**
- **Base URL**: `https://openlibrary.org/`
- **Search Endpoint**: `/search/books.json`
- **Work Details**: `/works/{workId}.json`
- **Author Details**: `/authors/{authorId}.json`

### **API Features**
- **Rate Limiting**: Proper delay antara requests
- **User-Agent**: Custom user agent untuk compliance
- **Timeout Configuration**: 15-20 second timeouts
- **Error Handling**: Comprehensive error states

### **Data Models**
- **WorkDetail**: Comprehensive book information
- **AuthorDetail**: Author information dengan bio
- **SubjectDetail**: Subject categorization
- **Referenced Data**: Auto-fetch semua referenced entities

## 🎨 UI Components

### **SearchBar**
- **Collapsible Design**: Expand/collapse dengan animasi
- **Horizontal Animation**: Smooth expand dari kiri ke kanan
- **Auto-focus**: Focus otomatis saat expand
- **Clear Function**: Clear search dengan icon

### **FilterSection**
- **Sort Options**: Relevansi, Edisi Terbaru, Klasik, Acak
- **Animated Visibility**: Muncul/hilang dengan search bar
- **Filter Chips**: Material Design filter chips
- **Loading States**: Loading indicator untuk sort operations

### **BookCard**
- **Cover Image**: Async image loading dengan Coil
- **Book Info**: Title, author, year, publisher
- **Favorite Button**: Heart icon dengan toggle functionality
- **Click Handling**: Navigasi ke book details

### **Loading & Error States**
- **LoadingState**: Circular progress dengan message
- **ErrorState**: Error display dengan retry button
- **EmptyState**: Empty state dengan refresh option

## 🔄 State Management

### **ViewModel States**
- **Search State**: Loading, Success, Error states
- **Book List State**: Pagination dan load more
- **Favorites State**: Local storage management
- **UI State**: Search expanded, selected tab

### **Data Flow**
```
User Input → ViewModel → Repository → API/Local → UI Update
```

### **Caching Strategy**
- **API Cache**: 5-minute TTL untuk referenced data
- **Local Storage**: Room database untuk favorites
- **Memory Cache**: In-memory caching untuk performance

## 📱 User Experience

### **Search Flow**
1. **Initial State**: Search bar collapsed (hanya icon)
2. **Expand**: Klik icon search → search bar expand horizontal
3. **Input**: User ketik query → real-time search
4. **Results**: Book list update dengan search results
5. **Collapse**: Klik icon close → search bar collapse

### **Navigation Flow**
1. **Home Screen**: Search bar + 2 tabs (Book List, Favorites)
2. **Book List**: Infinite scroll dengan load more
3. **Book Details**: Comprehensive book information
4. **Settings**: Theme toggle dan preferences

### **Filter & Sort**
1. **Search Expanded**: Filter section muncul otomatis
2. **Sort Options**: Pilih sort criteria
3. **Real-time Update**: Book list update sesuai sort
4. **Persistent State**: Sort preference tersimpan

## 🚧 Troubleshooting

### **Common Issues**
- **Force Close**: Pastikan ViewModel dependencies ter-setup dengan benar
- **Search Not Working**: Cek internet connection dan API status
- **Images Not Loading**: Verifikasi Coil configuration
- **Build Errors**: Clean project dan sync Gradle

### **Debug Tips**
- **Logcat**: Monitor log untuk error details
- **Network Inspector**: Cek API calls dan responses
- **Layout Inspector**: Debug UI layout issues
- **Database Inspector**: Monitor Room database operations

## 🤝 Contributing

1. Fork repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **OpenLibrary**: Free book data API
- **Jetpack Compose**: Modern Android UI toolkit
- **Material Design**: Design system guidelines
- **Android Community**: Open source contributions
---

**BookFinder** - Discover the world of books with modern Android technology! 📚✨
