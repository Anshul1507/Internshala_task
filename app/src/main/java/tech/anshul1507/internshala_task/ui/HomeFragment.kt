package tech.anshul1507.internshala_task.ui

import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomsheet.BottomSheetBehavior
import tech.anshul1507.internshala_task.MainActivity
import tech.anshul1507.internshala_task.R
import tech.anshul1507.internshala_task.adapter.NoteAdapter
import tech.anshul1507.internshala_task.adapter.NotesItemClickListener
import tech.anshul1507.internshala_task.databinding.FragmentHomeBinding
import tech.anshul1507.internshala_task.entity.NoteModel


class HomeFragment : Fragment(), NotesItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: NoteAdapter
    private lateinit var mailID: String
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

        MainActivity.acct =
            GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext) as GoogleSignInAccount
        mailID = MainActivity.acct.email.toString()
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
            if (noteList.isEmpty()) {
                binding.emptyNoteLayout.visibility = View.VISIBLE
                binding.rv.visibility = View.GONE

            } else {
                binding.emptyNoteLayout.visibility = View.GONE
                binding.rv.visibility = View.VISIBLE
            }
            it.let {
                adapter.updateList(noteList)
            }
        })

        binding.bottomSheetLayout.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        //check listener for changing fab icon
        checkInputTextChangeListener()

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

            } else {
                if (binding.etNoteTitle.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Enter title", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Enter note", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun rotateAddBtn(v: View, rotate: Boolean): Boolean {
        v.animate().setDuration(400)
            .setListener(object : AnimatorListenerAdapter() {
            })
            .rotation(if (rotate) 360f else 0f)
        return rotate
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
                //Share option
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_SUBJECT, "Share via")

                val shareMessage =
                    "Note title: ${note.title} \nNote: ${note.text} \n~ ${MainActivity.acct.givenName}"
                share.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(share, "Share this note to someone"))
            }
        }
    }

    private fun checkInputTextChangeListener() {
        binding.etNoteTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty() && binding.etNoteText.text.isNotEmpty()) {
                    rotateAddBtn(binding.btnAddNote, true)
                    binding.btnAddNote.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_save_task_white
                        )
                    )
                } else {
                    rotateAddBtn(binding.btnAddNote, false)
                    binding.btnAddNote.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_add_task_white
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.etNoteText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty() && binding.etNoteTitle.text.isNotEmpty()) {
                    rotateAddBtn(binding.btnAddNote, true)
                    binding.btnAddNote.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_save_task_white
                        )
                    )
                } else {
                    rotateAddBtn(binding.btnAddNote, false)
                    binding.btnAddNote.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_add_task_white
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.title =
            "Hey ${MainActivity.acct.givenName}"
    }
}