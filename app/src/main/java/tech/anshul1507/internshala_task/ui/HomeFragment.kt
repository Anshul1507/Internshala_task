package tech.anshul1507.internshala_task.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import tech.anshul1507.internshala_task.MainActivity
import tech.anshul1507.internshala_task.adapter.NotesItemClickListener
import tech.anshul1507.internshala_task.adapter.NoteAdapter
import tech.anshul1507.internshala_task.databinding.FragmentHomeBinding
import tech.anshul1507.internshala_task.entity.NoteModel

class HomeFragment : Fragment(), NotesItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: NoteAdapter
    private var mailID = "test@gmail.com"
    private lateinit var homeViewModel: HomeFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

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

        var cnt = 0
        binding.btnAddNote.setOnClickListener {
            val noteItem = NoteModel("title $cnt", "text $cnt", mailID)
            homeViewModel.insertNode(noteItem)
            cnt++
        }

    }

    override fun onItemClicked(note: NoteModel) {
        note.text = "text"
        note.title = "title"

        homeViewModel.updateNode(note)
    }

}