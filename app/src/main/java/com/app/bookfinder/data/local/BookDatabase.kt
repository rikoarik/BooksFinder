package com.app.bookfinder.data.local

import androidx.room.*
import com.app.bookfinder.data.model.Author
import com.app.bookfinder.data.model.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(
    entities = [Book::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    
    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null
        
        fun getDatabase(context: android.content.Context): BookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "book_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromAuthorList(value: List<Author>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toAuthorList(value: String?): List<Author>? {
        return value?.let {
            val type = object : TypeToken<List<Author>>() {}.type
            gson.fromJson(it, type)
        }
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }
    
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.let {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
