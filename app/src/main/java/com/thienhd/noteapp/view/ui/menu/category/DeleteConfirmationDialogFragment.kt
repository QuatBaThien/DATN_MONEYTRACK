package com.thienhd.noteapp.view.ui.menu.category

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.DialogDeleteConfirmationBinding

class DeleteConfirmationDialogFragment(
    private val fragmentCheck: String,
    private val onDeleteConfirmed: () -> Unit,
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogDeleteConfirmationBinding.inflate(LayoutInflater.from(context))

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(binding.root)

        val dialog = builder.create()

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnDelete.setOnClickListener {
            onDeleteConfirmed()
            if (fragmentCheck != "EditTransactionFragment")
                findNavController().navigateUp()
            dialog.dismiss()
        }

        return dialog
    }
}
