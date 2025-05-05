package com.codewithajaj.ecommerce.fragments

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.codewithajaj.ecommerce.activity.LoginActivity
import com.codewithajaj.ecommerce.activity.MyProfile
import com.codewithajaj.ecommerce.activity.OrderHistoryActivity
import com.codewithajaj.ecommerce.activity.SavedAddressActivity
import com.codewithajaj.ecommerce.databinding.FragmentAccountBinding



class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        binding.myaccountProfile.setOnClickListener {
            val intent = Intent(requireContext(), MyProfile::class.java)
            startActivity(intent)
        }

        binding.savedaddressProfile.setOnClickListener {
            val intentProfile = Intent(requireContext(), SavedAddressActivity ::class.java)
            startActivity(intentProfile)
        }
        // logout the acc
        binding.logOutTV.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())

            builder.setMessage("Do You Want To logOut")
            builder.setPositiveButton("YES") { _, _ ->
                val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

            builder.create().show()
        }

        binding.orderHistoryTV.setOnClickListener {
            val intent = Intent(requireContext(), OrderHistoryActivity::class.java)
            startActivity(intent)
        }

    }
}
