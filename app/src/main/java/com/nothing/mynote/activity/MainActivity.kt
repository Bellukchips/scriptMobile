package com.nothing.mynote.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nothing.mynote.R
import com.nothing.mynote.activity.SettingsActivity.Companion.PREF_NAME
import com.nothing.mynote.adapter.NoteAdapter
import com.nothing.mynote.db.NoteHelper
import com.nothing.mynote.helper.MappingHelper
import com.nothing.mynote.model.Note
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NoteAdapter

    private lateinit var noteHelper: NoteHelper
    private var sharedPreferences: SharedPreferences? = null
    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
//        private const val PREF_NAME ="MODEDARK"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = getString(R.string.Note)
        rv_notes.layoutManager = LinearLayoutManager(this)
        rv_notes.setHasFixedSize(true)
        adapter = NoteAdapter(this)
//        sharedPreferences = getSharedPreferences(PREF_NAME,0)
//        val modeDark =sharedPreferences!!.getBoolean("darkMode",false)
        fab_add.setOnClickListener {
            val intent = Intent(this@MainActivity, NoteUpdateActivity::class.java)
            startActivityForResult(intent, NoteUpdateActivity.REQUEST_ADD)
        }
        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()
        //get data
        if (savedInstanceState == null) {
//                if (modeDark){
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                }else{
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//
//                }


            // proses ambil data
            loadNotesAsync()
        } else {

//                if (modeDark){
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                }else{
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//
//                }


            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNote = list
            }
        }
        rv_notes.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//         if(item?.itemId == R.id.copyright){
//            val i = Intent(this@MainActivity,AboutActivity::class.java)
//            startActivity(i)        }
            if(item?.itemId == R.id.settings){
                val i = Intent(this@MainActivity,SettingsActivity::class.java)
                startActivity(i)
            }
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

    override fun onStart() {
        sharedPreferences = getSharedPreferences(PREF_NAME,0)
        val modeDark = sharedPreferences!!.getBoolean("darkMode",false)
        if (modeDark){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onStart()
    }

    override fun onResume() {
       loadNotesAsync()
        super.onResume()
    }
    override fun onDestroy() {
        noteHelper.close()
        super.onDestroy()
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


