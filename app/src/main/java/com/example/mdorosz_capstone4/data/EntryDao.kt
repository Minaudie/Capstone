package com.example.mdorosz_capstone4.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: Entry): Long

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    //select specific entry
    @Query("SELECT * FROM entry WHERE id = :id")
    fun getEntryById(id: Int): Flow<Entry>

    //get all entries, ascending order by creation date
    @Query("SELECT * FROM entry ORDER BY date ASC")
    fun getAllEntries(): Flow<List<Entry>>
}
