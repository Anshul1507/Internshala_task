package tech.anshul1507.internshala_task.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.item_notes.*
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
    private var preTitle: String = "Title"
    private var preText: String = "Text"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        bottomSheetBehavior = BottomSheetBehavior.from<View>(binding.bottomSheetLayout)

        binding.rv.layoutManager = LinearLayoutManager(context!!.applicationContext)
        adapter = NoteAdapter(context!!.applicationContext, this)
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
        adapter = NoteAdapter(requireContext(), this)
        binding.rv.adapter = adapter

        allNotes.observe(requireActivity(), {
            val noteList: List<NoteModel> = allNotes.value!!
            it.let {
                adapter.updateList(noteList)
            }
        })

        //todo get Intent for edit match and match if case
        binding.etNoteTitle.setText(preTitle)
        binding.etNoteText.setText(preText)

        binding.bottomSheetLayout.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        var cnt = 0
        binding.btnAddNote.setOnClickListener {
            //if bottom sheet expanded -> collapse on add/edit note
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val noteItem = NoteModel("title $cnt", "text $cnt", mailID)
                homeViewModel.insertNode(noteItem)
                cnt++
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

        }

    }

    override fun onItemClicked(note: NoteModel) {
        note.text = "text"
        note.title = "title"

        homeViewModel.updateNode(note)
    }

}