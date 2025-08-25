package com.app.bookfinder

import android.app.Application
import com.app.bookfinder.data.local.BookDatabase

class BookFinderApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        BookDatabase.getDatabase(this)
    }
}
