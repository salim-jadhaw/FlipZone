package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codewithajaj.ecommerce.PreferenceManager
import com.codewithajaj.ecommerce.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager : PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        preferenceManager = PreferenceManager(this@MainActivity)

        val intent = if (preferenceManager.isUserLoggedIn()) {
            Intent(this, DashBoardActivity::class.java) // Redirect to Home if logged in
        } else {
            Intent(this, LoginActivity::class.java) // Redirect to Login if not
        }
        startActivity(intent)
        finish()
    }
}