package com.codewithajaj.ecommerce.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codewithajaj.ecommerce.Adapter.AdapterForTabs
import com.codewithajaj.ecommerce.databinding.ActivityDashBoard2Binding
import com.google.android.material.tabs.TabLayoutMediator

class DashBoardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDashBoard2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoard2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val fragmentAdapter = AdapterForTabs(this@DashBoardActivity)

        binding.viewPager2.currentItem = 2
        binding.viewPager2.adapter = fragmentAdapter
        binding.viewPager2.isUserInputEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager2
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Home"
                }
                1 -> {
                    tab.text = "Cart"
                }
                2 -> {
                    tab.text = "Profile"
                }
            }
        }.attach()
    }
}