package com.nothing.mynote.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nothing.mynote.R
import com.nothing.mynote.adapter.NoteAdapter
import com.nothing.mynote.db.NoteHelper
import com.nothing.mynote.helper.MappingHelper
import com.nothing.mynote.model.Note
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: NoteAdapter
    private lateinit var noteHelper: NoteHelper
    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = getString(R.string.Note)
        rv_notes.layoutManager = LinearLayoutManager(this)
        rv_notes.setHasFixedSize(true)
        adapter = NoteAdapter(this)
        rv_notes.adapter = adapter

        fab_add.setOnClickListener {
            val intent = Intent(this@MainActivity, NoteUpdateActivity::class.java)
            startActivityForResult(intent, NoteUpdateActivity.REQUEST_ADD)
        }
        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()

        //get data
        if (savedInstanceState == null) {
            // proses ambil data
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNote = list
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
         if(item?.itemId == R.id.copyright){
            val i = Intent(this@MainActivity,AboutActivity::class.java)
            startActivity(i)        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_about,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNote)
    }
    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            progressbar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = noteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progressbar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0) {
                adapter.listNote = notes
            } else {
                adapter.listNote = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
        }}

    override fun onDestroy() {
        super.onDestroy()
        noteHelper.close()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                NoteUpdateActivity.REQUEST_ADD -> if (resultCode == NoteUpdateActivity.RESULT_ADD) {
                    val note = data.getParcelableExtra<Note>(NoteUpdateActivity.EXTRA_NOTE)
                    adapter.addItem(note)
                    rv_notes.smoothScrollToPosition(adapter.itemCount - 1)
                    showSnackbarMessage("Satu item berhasil ditambahkan")
                }
                NoteUpdateActivity.REQUEST_UPDATE ->
                    when (resultCode) {
                        NoteUpdateActivity.RESULT_UPDATE -> {
                            val note = data.getParcelableExtra<Note>(NoteUpdateActivity.EXTRA_NOTE)
                            val position = data.getIntExtra(NoteUpdateActivity.EXTRA_POSITION, 0)
                            adapter.updateItem(position, note)
                            rv_notes.smoothScrollToPosition(position)
                            showSnackbarMessage("Satu item berhasil diubah")
                        }
                        NoteUpdateActivity.RESULT_DELETE -> {
                            val position = data.getIntExtra(NoteUpdateActivity.EXTRA_POSITION, 0)
                            adapter.deleteItem(position)
                            showSnackbarMessage("Satu item berhasil dihapus")
                        }
                    }
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(rv_notes, message, Snackbar.LENGTH_SHORT).show()
    }

}


