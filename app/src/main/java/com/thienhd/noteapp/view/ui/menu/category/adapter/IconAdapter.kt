package com.thienhd.noteapp.view.ui.menu.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.ItemIconBinding

class IconAdapter(
    private val iconList: List<String>,
    private val initialSelectedIconIndex: Int?,
    private val iconClickListener: (String, Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    private var selectedIconIndex: Int? = initialSelectedIconIndex

    class IconViewHolder(private val binding: ItemIconBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(iconName: String, isSelected: Boolean, clickListener: (String, Int) -> Unit) {
            val context = binding.root.context
            val iconResId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
            binding.ivIcon.setImageResource(iconResId)
            binding.ivIcon.background = if (isSelected) {
                ContextCompat.getDrawable(context, R.drawable.selected_icon_border)
            } else {
                null
            }
            binding.root.setOnClickListener {
                clickListener(iconName, adapterPosition ) // iconIndex is adapterPosition + 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val binding = ItemIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IconViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconName = iconList[position]
        holder.bind(iconName, position == selectedIconIndex) { icon, index ->
            selectedIconIndex = position
            notifyDataSetChanged() // Update UI to show the selected icon
            iconClickListener(icon, index)
        }
    }

    override fun getItemCount(): Int {
        return iconList.size
    }

    fun getSelectedIcon(): String? {
        return selectedIconIndex?.let { iconList[it] }
    }
}
