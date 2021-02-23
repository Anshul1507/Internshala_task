package tech.anshul1507.internshala_task.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import tech.anshul1507.internshala_task.adapter.NoteAdapter
import tech.anshul1507.internshala_task.adapter.NotesItemClickListener
import tech.anshul1507.internshala_task.databinding.FragmentHomeBinding
import tech.anshul1507.internshala_task.entity.NoteModel

class HomeFragment : Fragment(), NotesItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: NoteAdapter
    private var mailID = "test@gmail.com"
    private lateinit var homeViewModel: HomeFragmentViewModel

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private var editNoteFrag: Boolean = false
    private lateinit var noteItem: NoteModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        bottomSheetBehavior = BottomSheetBehavior.from<View>(binding.bottomSheetLayout)

        binding.rv.layoutManager = LinearLayoutManager(context!!.applicationContext)
        adapter = NoteAdapter(this)
        binding.rv.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(HomeFragmentViewModel::class.java)

        val allNotes: LiveData<List<NoteModel>> = homeViewModel.getAllNotes(mailID)

        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = NoteAdapter(this)
        binding.rv.adapter = adapter

        allNotes.observe(requireActivity(), {
            val noteList: List<NoteModel> = allNotes.value!!
            it.let {
                adapter.updateList(noteList)
            }
        })

        binding.bottomSheetLayout.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        binding.btnAddNote.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            if (binding.etNoteTitle.text.isNotEmpty() && binding.etNoteText.text.isNotEmpty()) {
                if (editNoteFrag) {
                    //edit created instance
                    noteItem.title = binding.etNoteTitle.text.toString()
                    noteItem.text = binding.etNoteText.text.toString()
                } else {
                    //In Add case, create new instance
                    noteItem = NoteModel(
                        binding.etNoteTitle.text.toString(),
                        binding.etNoteText.text.toString(),
                        mailID
                    )
                }

                //if bottom sheet expanded -> collapse on add/edit note
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    if (editNoteFrag) {
                        homeViewModel.updateNode(noteItem)
                    } else {
                        homeViewModel.insertNode(noteItem)
                    }
                    binding.etNoteTitle.setText("")
                    binding.etNoteText.setText("")

                }

            }
        }
    }

    override fun onItemClicked(btnID: String, note: NoteModel) {
        noteItem = note
        if (btnID == "edit" && bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        when (btnID) {
            "edit" -> {
                editNoteFrag = true
                binding.etNoteTitle.setText(note.title)
                binding.etNoteText.setText(note.text)
            }
            "delete" -> {
                homeViewModel.deleteNode(note)
            }
            "share" -> {
                //todo:: share related things
            }
        }
    }

}