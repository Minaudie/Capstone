package com.example.mdorosz_capstone4.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Entry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val entryId: Int = 0,
    @ColumnInfo(name = "title")
    val entryTitle: String,
    @ColumnInfo
    val date: String,
    @ColumnInfo(name = "bg_color")
    val bgColor: Int
)

/**
 * Places you have to add things to when you make a new entity
 *
 * JournalRoomDatabase @Database
 *
 * JournalViewModel parameters
 *      any file that instantiates a JournalViewModel
 */
