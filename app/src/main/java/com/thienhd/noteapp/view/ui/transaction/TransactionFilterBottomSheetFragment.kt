package com.thienhd.noteapp.view.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.ui.graphics.Color
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thienhd.noteapp.R


class TransactionFilterBottomSheetFragment : BottomSheetDialogFragment() {
    var incomeFilter = 0
    var timeFilter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_filter_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.bt_income).setOnClickListener {
            incomeFilter = 1
        }
        view.findViewById<Button>(R.id.bt_expenses).setOnClickListener {
            incomeFilter = 2
        }
        view.findViewById<Button>(R.id.bt_new).setOnClickListener {
            timeFilter = 1
        }
        view.findViewById<Button>(R.id.bt_old).setOnClickListener {
            timeFilter = 2
        }
        when (incomeFilter) {
            1 -> {
                
            }
        }
        view.findViewById<Button>(R.id.btn_apply).setOnClickListener {
            // Handle filter logic here

            // Dismiss bottom sheet
            dismiss()
        }
    }
}