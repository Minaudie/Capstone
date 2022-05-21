package com.example.mdorosz_capstone4.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageObjectDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(imageObject: ImageObject): Long

    @Update
    suspend fun update(imageObject: ImageObject)

    @Delete
    suspend fun delete(imageObject: ImageObject)

    //multimap return type to get all image objects for a specific entry
    //https://developer.android.com/training/data-storage/room/relationships#multimap
    //I don't think I actually need a map though???
    @Query("SELECT * FROM imageobject WHERE imageobject.entry_id = :entryId")
    suspend fun getAllImageObjectsByEntry(entryId: Int): MutableList<ImageObject>
}