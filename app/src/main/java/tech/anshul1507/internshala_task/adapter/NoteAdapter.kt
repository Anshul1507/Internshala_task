package tech.anshul1507.internshala_task.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.anshul1507.internshala_task.R
import tech.anshul1507.internshala_task.entity.NoteModel

class NoteAdapter(private val context: Context, private val listener: NotesItemClickListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val allNotes = ArrayList<NoteModel>()

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val text = itemView.findViewById<TextView>(R.id.text)
//        val option = itemView.findViewById<ImageView>(R.id.item_option)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val viewHolder = NoteViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_notes,
                parent,
                false
            )
        )
//        viewHolder.option.setOnClickListener {
//            listener.onItemClicked(allNotes[viewHolder.adapterPosition])
//        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val curNote = allNotes[position]
        holder.text.text = curNote.text
        holder.title.text = curNote.title
    }

    override fun getItemCount(): Int {
        return allNotes.size
    }


    fun updateList(newList: List<NoteModel>) {
        allNotes.clear()
        allNotes.addAll(newList)

        notifyDataSetChanged()
    }
}

interface NotesItemClickListener {
    fun onItemClicked(note: NoteModel)
}