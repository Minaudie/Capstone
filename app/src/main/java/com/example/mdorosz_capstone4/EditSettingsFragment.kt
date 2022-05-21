package com.example.mdorosz_capstone4

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mdorosz_capstone4.data.Entry
import com.example.mdorosz_capstone4.databinding.EditSettingsFragmentBinding
import kotlin.random.Random

class EditSettingsFragment : Fragment() {

    private lateinit var app: JournalApplication

    private val viewModel: JournalViewModel by activityViewModels {
        JournalViewModelFactory(
            app.database.entryDao(), app.database.imageObjectDao()
        )
    }

    lateinit var entry: Entry

    private val navigationArgs: ViewEntryFragmentArgs by navArgs()

    private var _binding: EditSettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditSettingsFragmentBinding.inflate(inflater, container, false)
        app = activity?.application as JournalApplication
        return binding.root
    }

    private fun isEntryValid():Boolean {
        return viewModel.isEntryValid(
            binding.entryTitle.text.toString()
        )
    }

    private fun addNewEntry() {
        if(isEntryValid()) {
            viewModel.addNewEntry(
                binding.entryTitle.text.toString(),
                "(strftime('%s','now'))",
                viewModel.randomizeBgColor()
            )
            findNavController().navigate(EditSettingsFragmentDirections
                .actionEditSettingsFragmentToListFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.entryId
        if(id > 0) {
            //editing existing item
            viewModel.retrieveEntry(id).observe(this.viewLifecycleOwner) { selectedItem ->
                entry = selectedItem
                bind(entry)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    //used to update the entry
    private fun bind(entry: Entry) {
        binding.apply {
            entryTitle.setText(entry.entryTitle, TextView.BufferType.SPANNABLE)
            saveButton.setOnClickListener { updateEntry() }
        }
    }

    private fun updateEntry() {
        //if(isEntryValid()) {
            viewModel.updateEntry(
                this.navigationArgs.entryId,
                this.binding.entryTitle.text.toString(),
                this.entry.date,
                this.entry.bgColor
            )
            val action = EditSettingsFragmentDirections.actionEditSettingsFragmentToListFragment()
            findNavController().navigate(action)
        //}
    }

}