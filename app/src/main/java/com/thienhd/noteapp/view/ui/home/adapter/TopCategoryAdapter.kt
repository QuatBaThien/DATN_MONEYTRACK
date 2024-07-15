package com.thienhd.noteapp.view.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.databinding.ItemMaxCategoryBinding
import com.thienhd.noteapp.viewmodel.CategoryViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

class TopCategoryAdapter(
    private var categories: List<Triple<Int, Double, Double>>,
    private val categoryViewModel: CategoryViewModel
) : RecyclerView.Adapter<TopCategoryAdapter.TopCategoryViewHolder>() {
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    var iconName = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopCategoryViewHolder {
        val binding = ItemMaxCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopCategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun submitList(newCategories: List<Triple<Int, Double, Double>>) {
        categories = newCategories
        notifyDataSetChanged()
    }

   inner class TopCategoryViewHolder(private val binding: ItemMaxCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Triple<Int, Double, Double>) {
            val category = categoryViewModel.getCategoryById(item.first)
            binding.tvCategoryName.text = category?.name
            binding.tvCategoryAmount.text = numberFormat.format(item.second) + " VNĐ"
            binding.tvPrecent.text = (item.second/item.third*100).roundToInt().toString() + "%"
            binding.apply {
                when (item.first){
                    -3 -> {
                        tvCategoryName.text = "Vay tiền"
                        val iconResId = root.context.resources.getIdentifier("ic_item_debt", "drawable", root.context.packageName)
                        ivCategoryIcon.setImageResource(iconResId)
                    }
                    -4 -> {
                        tvCategoryName.text = "Cho vay"
                        val iconResId = root.context.resources.getIdentifier("ic_item_loan", "drawable", root.context.packageName)
                        ivCategoryIcon.setImageResource(iconResId)
                    }
                    -5 -> {
                        tvCategoryName.text = "Trả nợ"
                        val iconResId = root.context.resources.getIdentifier("ic_item_paid_debt", "drawable", root.context.packageName)
                       ivCategoryIcon.setImageResource(iconResId)
                    }
                    -6 -> {
                        tvCategoryName.text = "Thu nợ"
                        val iconResId = root.context.resources.getIdentifier("ic_item_get_paid", "drawable", root.context.packageName)
                        ivCategoryIcon.setImageResource(iconResId)
                    }

                    else -> {
                        val iconName = "ic_item_${category?.iconId}"
                        tvCategoryName.text = category?.name
                        val iconResId = root.context.resources.getIdentifier(iconName, "drawable", root.context.packageName)
                        ivCategoryIcon.setImageResource(iconResId)
                    }
                }
            }
        }
    }
}
