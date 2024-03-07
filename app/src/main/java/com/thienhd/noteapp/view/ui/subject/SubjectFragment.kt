package com.thienhd.noteapp.view.ui.subject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.thienhd.noteapp.databinding.FragmentSubjectBinding
import com.thienhd.noteapp.viewModel.SubjectViewModel

class SubjectFragment : Fragment() {

    private var _binding: FragmentSubjectBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val subjectViewModel =
            ViewModelProvider(this)[SubjectViewModel::class.java]

        _binding = FragmentSubjectBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}