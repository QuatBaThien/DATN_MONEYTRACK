package com.thienhd.noteapp.view.ui.welcome

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.thienhd.noteapp.R
import com.thienhd.noteapp.view.ui.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeFragment: Fragment() {
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            withContext(Dispatchers.Main) {
                if (auth.currentUser == null) {
                    findNavController().navigate(R.id.nav_welcome_to_splash1)
                } else {
                    findNavController().navigate(R.id.nav_welcome_to_main)
                }
            }
        }
    }
}
