package com.nothing.mynote.helper

import android.database.Cursor
import com.nothing.mynote.db.DatabaseContract.NoteColumns.Companion.DATE
import com.nothing.mynote.db.DatabaseContract.NoteColumns.Companion.DESC
import com.nothing.mynote.db.DatabaseContract.NoteColumns.Companion.TITLE
import com.nothing.mynote.db.DatabaseContract.NoteColumns.Companion._ID
import com.nothing.mynote.model.Note

object MappingHelper {
    fun mapCursorToArrayList(notesCursor: Cursor): ArrayList<Note> {
        val notesList = ArrayList<Note>()
        while (notesCursor.moveToNext()) {
            val id = notesCursor.getInt(notesCursor.getColumnIndexOrThrow(_ID))
            val title = notesCursor.getString(notesCursor.getColumnIndexOrThrow(TITLE))
            val description = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DESC))
            val date = notesCursor.getString(notesCursor.getColumnIndexOrThrow(DATE))
            notesList.add(Note(id, title, description, date))
        }
        return notesList
    }
}