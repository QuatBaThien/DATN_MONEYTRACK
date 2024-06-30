package com.thienhd.noteapp.view.ui.splashScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.FragmentSplashStep1Binding


class SplashFragmentStep1 : Fragment() {
    lateinit var binding: FragmentSplashStep1Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashStep1Binding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bNext.setOnClickListener {
                findNavController().navigate(R.id.nav_splash1_to_register)
            }
        binding.bHadAccount.setOnClickListener {
            findNavController().navigate(R.id.nav_splash1_to_login)
        }
    }
}