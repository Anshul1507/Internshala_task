package tech.anshul1507.internshala_task.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.anshul1507.internshala_task.databinding.ItemNotesBinding
import tech.anshul1507.internshala_task.entity.NoteModel

class NoteAdapter(private val listener: NotesItemClickListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val allNotes = ArrayList<NoteModel>()

    inner class NoteViewHolder(private val binding: ItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val curNote = allNotes[position]
            binding.text.text = curNote.text
            binding.title.text = curNote.title

            binding.editNoteLayout.setOnClickListener {
                listener.onItemClicked("edit", allNotes[position])
            }

            binding.deleteNoteLayout.setOnClickListener {
                listener.onItemClicked("delete", allNotes[position])
            }

            binding.shareNoteLayout.setOnClickListener {
                listener.onItemClicked("share", allNotes[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
        NoteViewHolder(
            ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(position)
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
    fun onItemClicked(buttonID: String, note: NoteModel)
}