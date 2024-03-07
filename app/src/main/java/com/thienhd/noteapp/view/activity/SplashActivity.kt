package com.thienhd.noteapp.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.thienhd.noteapp.R
import com.thienhd.noteapp.databinding.ActivitySplashBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var count = 0
    val dataSave = getSharedPreferences("firstLog", 0)

     // if (dataSave.getString("firstTime", "").toString() == "no") {
     //   val intent= Intent(this@SplashActivity, ActivityMain::class.java).putExtra("login",false)
     //   startActivity(intent)
     //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
     //   this@SplashActivity.finish()
     // }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.splash_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        binding.bNext.setOnClickListener {
            it.isEnabled = false

            GlobalScope.launch {
                // Delay for 1 second
                delay(1000)

                // Switch to the main thread and enable the button
                withContext(Dispatchers.Main) {
                    binding.bNext.isEnabled = true
                }
            count++
                when (count){
                    1-> navController.navigate(R.id.nav_splash_1_to_nav_splash_2)
                    2-> navController.navigate(R.id.nav_splash_2_to_nav_splash_3)
                    3-> {


                    }
                }
            }
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.splash_nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}