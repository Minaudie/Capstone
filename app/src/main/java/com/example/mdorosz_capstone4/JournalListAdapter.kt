package com.example.mdorosz_capstone4

import android.content.Context
import android.util.Log
import android.view.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mdorosz_capstone4.data.Entry
import com.example.mdorosz_capstone4.databinding.ListItemBinding

class JournalListAdapter(
    private val onItemClicked: (Entry) -> Unit,
    private val onItemLongClicked: (Entry) -> Unit
) : ListAdapter<Entry, JournalListAdapter.JournalViewHolder>(DiffCallback) {

    lateinit var currentItem: Entry

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        context = parent.context

        return JournalViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            //has to be set inside the listener, or was not selecting the right entry
            currentItem = getItem(position)
            onItemClicked(currentItem)
        }

        holder.itemView.setOnLongClickListener {
            currentItem = getItem(position)
            onItemLongClicked(currentItem)
            true
        }

        holder.bind(current)
    }

    companion object {
        //checks differences between items to only update ui that has changes
        private val DiffCallback = object : DiffUtil.ItemCallback<Entry>() {
            override fun areItemsTheSame(oldEntry: Entry, newEntry: Entry): Boolean {
                return oldEntry === newEntry
            }

            override fun areContentsTheSame(oldEntry: Entry, newEntry: Entry): Boolean {
                return oldEntry.entryTitle == newEntry.entryTitle
            }
        }
    }

    class JournalViewHolder(private var binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(entry: Entry) {
            binding.apply {
                //set entry title
                entryTitle.text = entry.entryTitle
                //set background of constraint based on entry background color
                when(entry.bgColor) {
                    R.color.backgroundColor1 -> cardView.setBackgroundResource(R.drawable.bg_color1)
                    R.color.backgroundColor2 -> cardView.setBackgroundResource(R.drawable.bg_color2)
                    R.color.backgroundColor3 -> cardView.setBackgroundResource(R.drawable.bg_color3)
                    R.color.backgroundColor4 -> cardView.setBackgroundResource(R.drawable.bg_color4)
                    R.color.backgroundColor5 -> cardView.setBackgroundResource(R.drawable.bg_color5)
                    R.color.backgroundColor6 -> cardView.setBackgroundResource(R.drawable.bg_color6)
                    R.color.backgroundColor7 -> cardView.setBackgroundResource(R.drawable.bg_color7)
                }
            }
        }
    }
}