package tech.anshul1507.internshala_task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import tech.anshul1507.internshala_task.databinding.ItemNotesBinding
import tech.anshul1507.internshala_task.entity.NoteModel
import java.util.*
import kotlin.collections.ArrayList


class NoteAdapter(private val listener: NotesItemClickListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val allNotes = ArrayList<NoteModel>()

    private var lastPosition = -1
    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val anim = ScaleAnimation(
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            anim.duration =
                Random().nextInt(501).toLong() //to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim)
            lastPosition = position
        }
    }

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
        // call Animation function
        setAnimation(holder.itemView, position)
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