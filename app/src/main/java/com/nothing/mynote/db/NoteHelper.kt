package com.nothing.mynote.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.nothing.mynote.db.DatabaseContract.NoteColumns.Companion.TABLE_NAME
import com.nothing.mynote.db.DatabaseContract.NoteColumns.Companion._ID

class NoteHelper(context: Context){
    private val dataBaseHelper: DatabaseHelper = DatabaseHelper(context)

    private lateinit var database: SQLiteDatabase

    companion object {
        private const val DATABASE_NAME = TABLE_NAME
        private var INSTANCE: NoteHelper? = null

        fun getInstance(context: Context): NoteHelper {
            if (INSTANCE == null) {
                synchronized(SQLiteOpenHelper::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = NoteHelper(context)
                    }
                }
            }
            return INSTANCE as NoteHelper
        }
    }
//    init {
//        //constructor
//        databaseHelper = DatabaseHelper(context)
//    }

    //method untuk buka tutup koneksi database
    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }
    fun close() {
        dataBaseHelper.close()
        if (database.isOpen)
            database.close()
    }

    //CRUD
    fun queryAll():Cursor{
        //ambil semua data
        return database.query(
            DATABASE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$_ID ASC"

        )

    }

    //ambil data berdasarkan ID
    fun gueryById(id: String): Cursor{
        return database.query(
            DATABASE_NAME,
            null,
            "$_ID = ?",
            arrayOf(id),
            null,
            null,
            null,
            null
        )
    }
    //query insert

    fun insertData(values: ContentValues?):Long{
        return database.insert(DATABASE_NAME,null,values)
    }
    //update
    fun updateData(id:String,values : ContentValues?):Int{
        return database.update(DATABASE_NAME,values, "$_ID = ?", arrayOf(id))
    }
    //delete

    fun deleteById(id:String):Int{
        return database.delete(DATABASE_NAME,"$_ID = '$id'",null)
    }
}