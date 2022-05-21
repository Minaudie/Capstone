package com.example.mdorosz_capstone4

import android.media.Image
import com.example.mdorosz_capstone4.data.EntryDao
import androidx.lifecycle.*
import com.example.mdorosz_capstone4.data.Entry
import com.example.mdorosz_capstone4.data.ImageObject
import com.example.mdorosz_capstone4.data.ImageObjectDao
import kotlinx.coroutines.*
import kotlin.IllegalArgumentException
import kotlin.random.Random

class JournalViewModel(
    private val entryDao: EntryDao,
    private val imageObjectDao: ImageObjectDao) : ViewModel() {

    /** Entry **/

    //variable for all items from database
    val allEntries: LiveData<List<Entry>> = entryDao.getAllEntries().asLiveData()

    //insert new record into database and get newly inserted ID
    private fun insertEntry(entry: Entry): Long = runBlocking {
        var returnedID = async { entryDao.insert(entry) }
        return@runBlocking returnedID.await()
    }

    //return a new instance of entry
    private fun getNewEntryInstance(entryTitle: String, date: String, bgColor: Int): Entry {
        return Entry(
            entryTitle = entryTitle,
            date = date,
            bgColor = bgColor
        )
    }

    //update entry in db
    private fun updateEntry(entry: Entry) {
        viewModelScope.launch { entryDao.update(entry) }
    }

    //update item
    private fun getUpdatedEntry(
        entryId: Int,
        entryTitle: String,
        date: String,
        bgColor: Int
    ): Entry {
        return Entry(
            entryId = entryId,
            entryTitle = entryTitle,
            date = date,
            bgColor = bgColor
        )
    }

    //public function to add new record to db
    //assigned date is handled in getNewEntryInstance
    fun addNewEntry(entryTitle: String, createdDate: String, bgColor: Int): Long {
        return insertEntry(getNewEntryInstance(entryTitle, createdDate, bgColor))
    }

    //checks if entered text is blank
    fun isEntryValid(entryTitle: String): Boolean {
        if(entryTitle.isBlank())
            return false
        return true
    }

    //public function to get specific entry from db
    fun retrieveEntry(id: Int): LiveData<Entry> {
        return entryDao.getEntryById(id).asLiveData()
    }

    //todo: also delete any image objects with this entry ID
    //public function to delete entry in db
    fun deleteEntry(entry: Entry) {
        viewModelScope.launch { entryDao.delete(entry) }
    }

    //public function to update entry in db
    fun updateEntry(
        entryId: Int,
        entryTitle: String,
        date: String,
        bgColor: Int
    ) {
        updateEntry(getUpdatedEntry(entryId, entryTitle, date, bgColor))
    }

    /** ImageObject **/

    //insert new image object into database and return newly inserted id
    private fun insertImageObject(imageObject: ImageObject): Long = runBlocking {
        var returnedID = async { imageObjectDao.insert(imageObject) }
        return@runBlocking returnedID.await()
    }

    //return new instance of image object
    //have to use string for image resource bc the id will change if there are more resources added
    private fun getNewImageObjectInstance(
        entryId: Int, imageResource: String, posX: Float, posY: Float,
        height: Int, width: Int, outline: Boolean): ImageObject {

        return ImageObject(
            entryId = entryId,
            imageResource = imageResource,
            posX = posX,
            posY = posY,
            height = height,
            width = width,
            outline = outline
        )
    }

    //update image object in db
    private fun updateImageObject(imageObject: ImageObject) {
        viewModelScope.launch { imageObjectDao.update(imageObject) }
    }

    //update image object
    private fun getUpdateImageObject(
        imageObjectId: Int,
        entryId: Int,
        imageResource: String,
        posX: Float,
        posY: Float,
        height: Int,
        width: Int,
        outline: Boolean
    ): ImageObject {
        return ImageObject(
            imageObjectId = imageObjectId,
            entryId = entryId,
            imageResource = imageResource,
            posX = posX,
            posY = posY,
            height = height,
            width = width,
            outline = outline
        )
    }

    //public function to add new image object to db
    fun addNewImageObject(
        entryId: Int,
        imageResource: String,
        posX: Float,
        posY: Float,
        height: Int,
        width: Int,
        outline: Boolean
    ): Long {
        return insertImageObject(
            getNewImageObjectInstance(entryId, imageResource, posX, posY, height, width, outline))
    }

    //public function to get delete image object in db
    fun deleteImageObject(imageObject: ImageObject) {
        viewModelScope.launch { imageObjectDao.delete(imageObject) }
    }

    //public function to update image object in db
    fun updateImageObject(
        imageObjectId: Int,
        entryId: Int,
        imageResource: String,
        posX: Float,
        posY: Float,
        height: Int,
        width: Int,
        outline: Boolean
    ) {
        updateImageObject(
            getUpdateImageObject(imageObjectId, entryId, imageResource, posX, posY, height, width, outline))
    }

    //public function to get all image objects by entry ID
    fun getAllImageObjectsByEntry(entryId: Int): MutableList<ImageObject>  = runBlocking {
        var returnedList = async { imageObjectDao.getAllImageObjectsByEntry(entryId) }
        return@runBlocking returnedList.await()
    }

    /** Other functions **/

    //public function to get a random background color
    fun randomizeBgColor(): Int {
        return when(Random.nextInt(8)) {
            1 -> R.color.backgroundColor1
            2 -> R.color.backgroundColor2
            3 -> R.color.backgroundColor3
            4 -> R.color.backgroundColor4
            5 -> R.color.backgroundColor5
            6 -> R.color.backgroundColor6
            7 -> R.color.backgroundColor7
            else -> R.color.backgroundColor1
        }
    }

}

/** ViewModelFactory **/

//used to instantiate JournalViewModel instance
class JournalViewModelFactory(
    private val entryDao: EntryDao,
    private val imageObjectDao: ImageObjectDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(entryDao, imageObjectDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}