package com.nothing.mynote.activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.nothing.mynote.R
import com.nothing.mynote.db.DatabaseContract
import com.nothing.mynote.db.DatabaseContract.NoteColumns.Companion.DATE
import com.nothing.mynote.db.NoteHelper
import com.nothing.mynote.model.Note
import kotlinx.android.synthetic.main.activity_note_update.*
import java.text.SimpleDateFormat
import java.util.*

class NoteUpdateActivity : AppCompatActivity(), View.OnClickListener{
    private var isEdit = false
    private var note:Note? = null
    private var position:Int = 0
    private lateinit var noteHelper: NoteHelper

    companion object{
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_update)
        noteHelper = NoteHelper.getInstance(applicationContext)

        note = intent.getParcelableExtra(EXTRA_NOTE)
        if (note != null){
            position = intent.getIntExtra(EXTRA_POSITION,0)
            isEdit =true
        }else{
            note = Note()
        }
        val actionBarTitle: String
        val btnTitle : String
        if (isEdit){
            actionBarTitle = getString(R.string.Update)
            btnTitle = getString(R.string.update)

            note?.let { edt_title.setText(it.title) }
            note?.let { edt_description.setText(it.desc) }
        }else{
            actionBarTitle  = getString(R.string.Insert)
            btnTitle = getString(R.string.Insert)

        }

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btn_submit.text = btnTitle

        btn_submit.setOnClickListener(this)
    }
    override fun onClick(view: View) {
        if (view.id == R.id.btn_submit) {
            val title = edt_title.text.toString().trim()
            val description = edt_description.text.toString().trim()

            if (title.isEmpty()) {
                edt_title.error = "Field can not be blank"
                return
            }

            note?.title = title
            note?.desc = description

            val intent = Intent()
            intent.putExtra(EXTRA_NOTE, note)
            intent.putExtra(EXTRA_POSITION, position)

            val values = ContentValues()
            values.put(DatabaseContract.NoteColumns.TITLE, title)
            values.put(DatabaseContract.NoteColumns.DESC, description)

            if (isEdit) {
                val result = noteHelper.updateData(note?.id.toString(), values).toLong()
                if (result > 0) {
                    setResult(RESULT_UPDATE, intent)
                    finish()
                } else {
                    Toast.makeText(this@NoteUpdateActivity, "Gagal mengupdate data", Toast.LENGTH_SHORT).show()
                }
            } else {
                note?.date = getCurrentDate()
                values.put(DATE, getCurrentDate())
                val result = noteHelper.insertData(values)

                if (result > 0) {
                    note?.id = result.toInt()
                    setResult(RESULT_ADD, intent)
                    finish()
                } else {
                    Toast.makeText(this@NoteUpdateActivity, "Gagal menambah data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?"
        } else {
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?"
            dialogTitle = "Hapus Note"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                if (isDialogClose) {
                    finish()
                } else {
                    val result = noteHelper.deleteById(note?.id.toString()).toLong()
                    if (result > 0) {
                        val intent = Intent()
                        intent.putExtra(EXTRA_POSITION, position)
                        setResult(RESULT_DELETE, intent)
                        finish()
                    } else {
                        Toast.makeText(this@NoteUpdateActivity, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Tidak") { dialog, id -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
