package com.thienhd.noteapp.view.ui.transaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.data.entities.Category
import com.thienhd.noteapp.databinding.ItemCategoryBinding

class ListCategoryAdapter(
    private var categoryList: List<Category>,
    private val onCategorySelected: (Category) -> Unit
) : RecyclerView.Adapter<ListCategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category, onCategorySelected: (Category) -> Unit) {
            binding.tvCategoryName.text = category.name
            val iconName = "ic_item_${category.iconId}"
            val iconResId = binding.root.context.resources.getIdentifier(iconName, "drawable", binding.root.context.packageName)
            binding.ivCategoryIcon.setImageResource(iconResId)

            binding.root.setOnClickListener {
                onCategorySelected(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position], onCategorySelected)
    }

    override fun getItemCount(): Int = categoryList.size

    fun submitList(newCategoryList: List<Category>) {
        categoryList = newCategoryList
        notifyDataSetChanged()
    }
}
