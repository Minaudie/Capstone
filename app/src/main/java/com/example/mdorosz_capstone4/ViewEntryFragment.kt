package com.example.mdorosz_capstone4

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mdorosz_capstone4.data.Entry
import com.example.mdorosz_capstone4.data.ImageObject
import com.example.mdorosz_capstone4.databinding.ViewEntryFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ViewEntryFragment : Fragment() {

    //far off TODO: func that are in here and edit entry, make them public here
    //and adjust edit entry functions to use those and then add the listeners

    //the entry that is displayed on screen
    lateinit var entry: Entry

    private lateinit var app: JournalApplication

    //view model set up
    private val viewModel: JournalViewModel by activityViewModels {
        JournalViewModelFactory(
            app.database.entryDao(), app.database.imageObjectDao()
        )
    }

    private val navigationArgs: ViewEntryFragmentArgs by navArgs()

    private var _binding: ViewEntryFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var layout: RelativeLayout
    private var imageObjectList = mutableListOf<ImageObject>()

    //will set the text on screen, uses view binding
    @RequiresApi(Build.VERSION_CODES.N)
    private fun bind(entry: Entry) {
        binding.apply {
            constraintLayout.setBackgroundColor(resources.getColor(entry.bgColor))

            imageObjectList = viewModel.getAllImageObjectsByEntry(navigationArgs.entryId)
            for(i in imageObjectList) {
                addImageView(i)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addImageView(i: ImageObject) {
        //create image view
        val iv = ImageView(context)

        iv.apply {
            //set tag == id of image object
            tag = i.imageObjectId
            //set image resource
            setImageResource(resources.getIdentifier(
                i.imageResource, "drawable", "com.example.mdorosz_capstone4"))
            //set layout parameters
            layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.width = i.width
            layoutParams.height = i.height

            //set position
            x = i.posX
            y = i.posY
        }

        //render view
        layout.addView(iv)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ViewEntryFragmentBinding.inflate(inflater, container, false)

        app = activity?.application as JournalApplication

        layout = binding.relativeLayout

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.entryId
        viewModel.retrieveEntry(id).observe(this.viewLifecycleOwner) { selectedItem ->
            entry = selectedItem
            bind(entry)
        }

        binding.editEntryFab.setOnClickListener {
            //it builds and works correctly with the errors???? I don't get it either
            val action =
                ViewEntryFragmentDirections.actionViewEntryFragmentToEditEntryFragment(entry.entryTitle, entry.entryId)
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}