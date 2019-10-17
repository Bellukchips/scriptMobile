package com.nothing.mynote.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nothing.mynote.Click.CustomOnItemClickListener
import com.nothing.mynote.R
import com.nothing.mynote.activity.NoteUpdateActivity
import com.nothing.mynote.model.Note
import kotlinx.android.synthetic.main.item_note.view.*

class NoteAdapter(private val activity: Activity) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    var listNote = ArrayList<Note>()
    set(listNote) {
        if (listNote.size >0){
            this.listNote.clear()
        }
        this.listNote.addAll(listNote)
        notifyDataSetChanged()
    }

    /*
    *  metode untuk menambahkan, memperbaharui dan menghapus Item di RecyclerView.
    * */

    fun addItem(note: Note){
        this.listNote.add(note)
        notifyItemInserted(this.listNote.size - 1)
    }

    fun updateItem(position: Int,note: Note){
        this.listNote[position] = note
        notifyItemChanged(position,note)
    }

    fun deleteItem(position: Int){
        this.listNote.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position,this.listNote.size)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(note: Note){
            with(itemView){
                tv_item_title.text = note.title
                tv_item_description.text = note.desc
                tv_date.text = "Create at " + note.date
                card_item.setOnClickListener(CustomOnItemClickListener(adapterPosition, object : CustomOnItemClickListener.OnItemClickCallback {
                    override fun onItemClicked(view: View, position: Int) {
                        val intent = Intent(activity, NoteUpdateActivity::class.java)
                        intent.putExtra(NoteUpdateActivity.EXTRA_POSITION, position)
                        intent.putExtra(NoteUpdateActivity.EXTRA_NOTE, note)
                        activity.startActivityForResult(intent, NoteUpdateActivity.REQUEST_UPDATE)
                    }
                }))

            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note,parent,false)
        return  NoteViewHolder(view)
    }

    override fun getItemCount(): Int=this.listNote.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(listNote[position])
    }

}