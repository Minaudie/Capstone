package com.example.mdorosz_capstone4

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mdorosz_capstone4.data.Entry
import com.example.mdorosz_capstone4.databinding.ListFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListFragment : Fragment() {

    //todo figure out how to change color of selection

    private lateinit var app: JournalApplication

    private val viewModel: JournalViewModel by activityViewModels {
        JournalViewModelFactory(
            app.database.entryDao(),
            app.database.imageObjectDao()
        )
    }

    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!

    private var actionMode: ActionMode? = null

    private lateinit var adapter: JournalListAdapter

    //action mode callback interface, for contextual menu
    private val actionModeCallback = object : ActionMode.Callback {
        //called when action mode is created
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.context_menu, menu)
            mode?.title = "Select"
            return true
        }

        //called each time action mode is shown
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when(item?.itemId) {
                R.id.edit_settings_cm -> {
                    mode?.finish()
                    val action = ListFragmentDirections.actionListFragmentToEditSettingsFragment(
                        "Edit Settings", adapter.currentItem.entryId)
                    findNavController().navigate(action)
                    return true
                }
                R.id.delete_entry_cm -> {
                    mode?.finish()
                    //delete entry
                    showConfirmationDialog()
                    return true
                }
                else -> return false
            }
        }

        //called when user exits action mode
        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ListFragmentBinding.inflate(inflater, container, false)
        app = activity?.application as JournalApplication
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //create adapter and set recycler view adapter
        adapter = JournalListAdapter( { //onItemClicked
            actionMode?.finish()

            //the fragment to go to, based on entry ID
            //it built with these errors, if I switch them it won't build??
            val action =
                ListFragmentDirections.actionListFragmentToViewEntryFragment(it.entryTitle, it.entryId)
            this.findNavController().navigate(action)
        }, { //onItemLongClicked

            if(actionMode != null) {
                false
            }
            actionMode = activity?.startActionMode(actionModeCallback)
        })
        binding.recyclerView.adapter = adapter
        viewModel.allEntries.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.fabNewEntry.setOnClickListener {
            actionMode?.finish()
            //create a new entry with generic information, returns the new ID
            //viewModel.randomizeBgColor is terrible. Consistently the same number over and over
            var newId = viewModel.addNewEntry("Untitled", "01/01/01", viewModel.randomizeBgColor())
            Log.v("List Frag", "new ID is $newId")
            //send user to view new entry (again, errors)
            val action = ListFragmentDirections.actionListFragmentToViewEntryFragment("Untitled", newId.toInt())
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
*/
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage("Are you sure you want to delete this entry?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                deleteItem()
            }
            .show()
    }

    private fun deleteItem() {
        viewModel.deleteEntry(adapter.currentItem)
    }
}