package com.example.mdorosz_capstone4

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.mdorosz_capstone4.data.Entry
import com.example.mdorosz_capstone4.data.ImageObject
import com.example.mdorosz_capstone4.databinding.EditEntryFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class EditEntryFragment : Fragment() {

    private lateinit var entry: Entry
    private lateinit var layout: RelativeLayout
    private lateinit var app: JournalApplication

    private var imageObjectList = mutableListOf<ImageObject>()

    private val viewModel: JournalViewModel by activityViewModels {
        JournalViewModelFactory(
            app.database.entryDao(), app.database.imageObjectDao()
        )
    }

    private var _binding: EditEntryFragmentBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs: EditEntryFragmentArgs by navArgs()

    //todo change the bottom sheet to grid
    //also todo add pages for swiping between things to add
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    //will set the text on screen, uses view binding
    @RequiresApi(Build.VERSION_CODES.N)
    private fun bind(entry: Entry) {
        binding.apply {
            frameLayout.setBackgroundColor(resources.getColor(entry.bgColor))

            //get all image objects by entry ID
            imageObjectList = viewModel.getAllImageObjectsByEntry(navigationArgs.entryId)
            //iterate through each object in the list
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

        //set up listener for drag
        iv.setOnLongClickListener {
            val myShadow = View.DragShadowBuilder(iv)
            iv.startDragAndDrop(null, myShadow, iv, 0)
            true
        }

        //render view
        layout.addView(iv)
    }

    //fragment load
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //view binding
        _binding = EditEntryFragmentBinding.inflate(inflater, container, false)
        //app var for viewModel
        app = activity?.application as JournalApplication

        //set relative layout to variable and set on drag listener
        layout = binding.relativeLayout
        layout.setOnDragListener { v, e ->
            dragListener(v, e)
        }

        return binding.root
    }

    //view is the relative layout, not the item being dragged
    private fun dragListener(v: View, e: DragEvent): Boolean {
        when(e.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                //set background color of frame layout to a different color
                //binding.frameLayout.setBackgroundColor(resources.getColor(R.color.primaryLightColor))
                //force redraw
                //binding.frameLayout.invalidate()
                return true
            }
            DragEvent.ACTION_DROP -> {
                //reset bg color
                //binding.frameLayout.setBackgroundColor(resources.getColor(entry.bgColor))
                //binding.frameLayout.invalidate()

                //get image view being dragged and change the location of the image
                if(e.localState == null) {
                    Log.e("EditEntry", "Local state null")
                } else {
                    val iv = e.localState as ImageView
                    changeImageLocation(iv, e)
                }

                return true
            }
            else -> {
                //Log.e("DragDrop", "Unknown action type of " + e.action.toString())
                return false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.entryId
        viewModel.retrieveEntry(id).observe(this.viewLifecycleOwner) { selectedItem ->
            entry = selectedItem
            bind(entry)
        }

        /*binding.addObjectFab.setOnClickListener {
            //add a new image object into db
            viewModel.addNewImageObject(
                navigationArgs.entryId, resources.getResourceEntryName(R.drawable.ic_baseline_eco_24),
                0F, 0F, 400, 400, false)

            //get list of all image objects from db
            imageObjectList = viewModel.getAllImageObjectsByEntry(navigationArgs.entryId)
            //create image view with latest image object
            addImageView(imageObjectList.last())
        }*/

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomConstraint)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> Log.v("EditEntry", "Bottom collapsed")
                    BottomSheetBehavior.STATE_EXPANDED -> Log.v("EditEntry", "Bottom expanded")
                    BottomSheetBehavior.STATE_DRAGGING -> Log.v("EditEntry", "Bottom dragging")
                    BottomSheetBehavior.STATE_SETTLING -> Log.v("EditEntry", "Bottom settling")
                    BottomSheetBehavior.STATE_HIDDEN -> Log.v("EditEntry", "Bottom hidden")
                    else -> Log.v("EditEntry", "Bottom else")
                }
            }
        })
        //persistent bottom sheet
        binding.bottomButton.setOnClickListener {
            if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.bottomButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.bottomButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

        //bottom sheet buttons
        binding.bottomSheet.stickerOpt1.setOnClickListener {
            viewModel.addNewImageObject(
                navigationArgs.entryId, resources.getResourceEntryName(R.drawable.ic_baseline_eco_24),
                0F, 0F, 200, 200, false)

            //get list of all image objects from db
            imageObjectList = viewModel.getAllImageObjectsByEntry(navigationArgs.entryId)

            //create image view with latest image object
            addImageView(imageObjectList.last())

            //send a click event to button that will close the bottom sheet
            binding.bottomButton.performClick()
        }

        binding.bottomSheet.stickerOpt2.setOnClickListener {
            viewModel.addNewImageObject(
                navigationArgs.entryId, resources.getResourceEntryName(R.drawable.ic_baseline_adb_24),
                0f,0f,200,200,false)

            imageObjectList = viewModel.getAllImageObjectsByEntry(navigationArgs.entryId)

            addImageView(imageObjectList.last())

            binding.bottomButton.performClick()
        }
    }

    //set the image coordinates to drag location
    //update database
    private fun changeImageLocation(v: ImageView, e: DragEvent) {
        //move the image view to the drop location
        //offset by half width/height to account for v.x/y starting top left and e.x/y being touch point/middle of object
        v.x = e.x - v.width/2
        v.y = e.y - v.height/2

        //get list of image objects that match the ID (should just be one)
        val currentImageObject = imageObjectList.filter { it.imageObjectId == v.tag }
        //get the image object
        val i = currentImageObject.first()
        //update the database entry
        viewModel.updateImageObject(
            i.imageObjectId, i.entryId, i.imageResource, v.x, v.y, v.height, v.width, i.outline)
        //refresh list
        imageObjectList = viewModel.getAllImageObjectsByEntry(navigationArgs.entryId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}