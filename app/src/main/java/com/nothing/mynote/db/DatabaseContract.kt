package com.nothing.mynote.db

import android.provider.BaseColumns

internal class DatabaseContract {
    internal class NoteColumns:BaseColumns{
        //declaration variable for field table
        companion object{
            const val TABLE_NAME ="note"
            const val _ID = "_id"
            const val TITLE = "title"
            const val DESC = "desc"
            const val DATE = "date"
        }
    }
}