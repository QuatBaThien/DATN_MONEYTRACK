package com.thienhd.noteapp.view.ui.menu.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.data.entities.Category
import com.thienhd.noteapp.databinding.ItemCategoryBinding


class CategoryAdapter(
    private var categories: List<Category>,
    private val itemClickListener: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category, clickListener: (Category) -> Unit) {
            val context = binding.root.context
            val iconName = "ic_item_${category.iconId}"
            val iconResId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
            binding.ivCategoryIcon.setImageResource(iconResId)
            binding.category = category
            binding.executePendingBindings()
            binding.root.setOnClickListener { clickListener(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun submitList(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
