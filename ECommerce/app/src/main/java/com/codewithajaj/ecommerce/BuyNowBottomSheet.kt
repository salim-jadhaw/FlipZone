package com.codewithajaj.ecommerce

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codewithajaj.ecommerce.ModelsClasses.PlaceOrderModel
import com.codewithajaj.ecommerce.activity.OrderSuccessfullyActivity
import com.codewithajaj.ecommerce.activity.SavedAddressActivity
import com.codewithajaj.ecommerce.databinding.BuyNowBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuyNowBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BuyNowBottomSheetBinding
    private lateinit var addressLauncher: ActivityResultLauncher<Intent>
    private var baseURL = "http://192.168.4.211/ecommerce/API/"

    companion object {
        private const val ARG_PRODUCT_NAME = "product_name"
        private const val ARG_PRODUCT_PRICE = "product_price"
        private const val ARG_PRODUCT_IMAGE = "product_image"
        private const val ARG_SHIPPING_ADDRESS = "shipping_address"

        fun newInstance(productName: String, productPrice: String, productImage: String, shippingAddress: String): BuyNowBottomSheet {
            return BuyNowBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_PRODUCT_NAME, productName)
                    putString(ARG_PRODUCT_PRICE, productPrice)
                    putString(ARG_PRODUCT_IMAGE, productImage)
                    putString(ARG_SHIPPING_ADDRESS, shippingAddress)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BuyNowBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("user_id", null).toString()

        // Initialize Address Selector
        addressLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val newAddress = result.data?.getStringExtra("selected_address")
                val newAddressId = result.data?.getStringExtra("selected_address_id")
                newAddress?.let {
                    binding.tvAddress.text = it
                    binding.tvAddress.tag = newAddressId
                }
            }
        }

        // Set Product Info
        arguments?.let {
            binding.tvProductName.text = it.getString(ARG_PRODUCT_NAME)
            binding.productPrice.text = it.getString(ARG_PRODUCT_PRICE)
            binding.tvTotalPrice.text = it.getString(ARG_PRODUCT_PRICE)

            Glide.with(this)
                .load(it.getString(ARG_PRODUCT_IMAGE))
                .placeholder(R.drawable.icon_search)
                .error(R.drawable.icon_shoppingcart)
                .into(binding.productImage)
        }

        // Close Bottom Sheet
        binding.ivClose.setOnClickListener { dismiss() }

        // Fetch and Display Default Address
        displayAddress(userId)

        // Change Address
        binding.tvChangeAddress.setOnClickListener {
            val intent = Intent(requireContext(), SavedAddressActivity::class.java)
            addressLauncher.launch(intent)
        }

        // Place Order
        binding.btnPlaceOrder.setOnClickListener {
            val addressId = binding.tvAddress.tag?.toString()
            if (addressId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select an address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            placeOrder(userId, addressId)
        }
    }

    private fun displayAddress(userId: String) {
        val apiService = RetrofitClient.getInstance(baseURL)
        val call = apiService.getshippingaddress("get_user_shipping_address", userId)

        call.enqueue(object : Callback<GetAddressResponse> {
            override fun onResponse(call: Call<GetAddressResponse>, response: Response<GetAddressResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.firstOrNull()?.let { address ->
                        val fullAddress = "${address.addressLine1}, ${address.city}, ${address.state}, ${address.postalCode}, ${address.country}"
                        binding.tvAddress.text = fullAddress
                        binding.tvAddress.tag = address.shippingAddressId.toString()
                    } ?: run {
                        Toast.makeText(requireContext(), "No address found. Please add one.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load address", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetAddressResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun placeOrder(userId: String, addressId: String) {
        val apiService = RetrofitClient.getInstance(baseURL)
        val call = apiService.placeOrder(
            "order",
            userId,
            addressId)

        call.enqueue(object : Callback<PlaceOrderModel> {
            override fun onResponse(call: Call<PlaceOrderModel>, response: Response<PlaceOrderModel>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.status == 200) {
                            Toast.makeText(requireContext(), "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                            dismiss() // Close the Bottom Sheet

                            val intent = Intent(requireContext(), OrderSuccessfullyActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), "First add to Cart, Your ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error placing order", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PlaceOrderModel>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
