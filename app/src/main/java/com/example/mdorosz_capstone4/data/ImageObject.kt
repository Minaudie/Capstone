package com.example.mdorosz_capstone4.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageObject(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "image_object_id")
    val imageObjectId: Int = 0,
    @ColumnInfo(name="entry_id")
    val entryId: Int,
    @ColumnInfo(name="image_resource")
    val imageResource: String,
    @ColumnInfo(name="pos_x")
    val posX: Float,
    @ColumnInfo(name = "pos_y")
    val posY: Float,
    @ColumnInfo(name="height")
    val height: Int,
    @ColumnInfo(name = "width")
    val width: Int,
    @ColumnInfo(name="outline")
    val outline: Boolean
)
