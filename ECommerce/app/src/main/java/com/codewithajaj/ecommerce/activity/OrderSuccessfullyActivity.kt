package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codewithajaj.ecommerce.databinding.ActivityOrderSuccessfullyBinding

class OrderSuccessfullyActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrderSuccessfullyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessfullyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        Handler(Looper.getMainLooper()).postDelayed( {
            binding.animationView.visibility = View.VISIBLE
            binding.animationView.playAnimation()
        }, 0)

        binding.btnContinueShopping.setOnClickListener {
            startActivity(Intent(this@OrderSuccessfullyActivity , DashBoardActivity::class.java))
            finish()
        }
    }
}