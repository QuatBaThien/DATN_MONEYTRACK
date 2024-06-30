package com.thienhd.noteapp.view.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thienhd.noteapp.databinding.ItemDateTransactionBinding
import com.thienhd.noteapp.data.DateTransaction


class TransactionListAdapter(private val dateTransactionlist: ArrayList<DateTransaction>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        lateinit var binding: ItemDateTransactionBinding
    inner class ViewHolder(val binding: ItemDateTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // Define click listener for the ViewHolder's View
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDateTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder){
            with(dateTransactionlist[position]){
                binding.tvTransactionDay.text = this.date
//                binding.rvTransactionList.adapter=
            }
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dateTransactionlist.size

}

