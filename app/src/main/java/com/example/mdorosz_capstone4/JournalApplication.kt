package com.example.mdorosz_capstone4

import android.app.Application
import com.example.mdorosz_capstone4.data.JournalRoomDatabase

class JournalApplication: Application() {
    val database: JournalRoomDatabase by lazy { JournalRoomDatabase.getDatabase(this) }
}