package com.codewithajaj.ecommerce.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codewithajaj.ecommerce.fragments.AccountFragment
import com.codewithajaj.ecommerce.fragments.CartFragment
import com.codewithajaj.ecommerce.fragments.HomeFragment

class AdapterForTabs(activity : FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position)  {
            0 -> HomeFragment()
            1 -> CartFragment()
            2 -> AccountFragment()
            else -> HomeFragment()
        }
    }
}