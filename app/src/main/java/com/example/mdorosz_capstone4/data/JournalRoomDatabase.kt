package com.example.mdorosz_capstone4.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Entry::class, ImageObject::class], version = 4, exportSchema = false)
abstract class JournalRoomDatabase: RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun imageObjectDao(): ImageObjectDao

    companion object {
        @Volatile
        private var INSTANCE: JournalRoomDatabase? = null

        fun getDatabase(context: Context): JournalRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JournalRoomDatabase::class.java,
                    "journal_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

