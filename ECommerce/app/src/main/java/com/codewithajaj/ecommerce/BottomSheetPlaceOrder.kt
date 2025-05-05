package com.codewithajaj.ecommerce

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.codewithajaj.ecommerce.Adapter.ShowCartProductAdapter
import com.codewithajaj.ecommerce.ModelsClasses.CartProductShowData
import com.codewithajaj.ecommerce.ModelsClasses.PlaceOrderModel
import com.codewithajaj.ecommerce.activity.OrderSuccessfullyActivity
import com.codewithajaj.ecommerce.activity.SavedAddressActivity
import com.codewithajaj.ecommerce.databinding.BottomSheetPlaceOrderBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomSheetPlaceOrder : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetPlaceOrderBinding
    private val baseURL = "http://192.168.4.211/ecommerce/API/"
    private lateinit var addressLauncher: ActivityResultLauncher<Intent>

    // Cart product list
    private val showCartProductList = mutableListOf<CartProductShowData>()

    private lateinit var showCartProductAdapter: ShowCartProductAdapter

    companion object {
        private const val ARG_PRODUCT_PRICE = "product_price"
        private const val ARG_ADDRESS = "address"
        private const val ARG_CART_ITEMS = "cart_items"

        fun newInstance(address: String, productPrice: String, cartItems: List<CartProductShowData>): BottomSheetPlaceOrder {
            val fragment = BottomSheetPlaceOrder()
            val args = Bundle()

            // Serialize cart items using Gson
            val cartItemsJson = Gson().toJson(cartItems)

            args.putString(ARG_PRODUCT_PRICE, productPrice)
            args.putString(ARG_ADDRESS, address)
            args.putString(ARG_CART_ITEMS, cartItemsJson)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetPlaceOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences?.getString("user_id", null).toString()

        showCartProductAdapter = ShowCartProductAdapter(requireContext(), showCartProductList)

        val productPrice = arguments?.getString(ARG_PRODUCT_PRICE) ?: "0"
        val initialAddress = arguments?.getString(ARG_ADDRESS) ?: "No address available"

        // Deserialize cart items
        val cartItemsJson = arguments?.getString(ARG_CART_ITEMS)
        val type = object : TypeToken<List<CartProductShowData>>() {}.type
        val cartItems: List<CartProductShowData> = Gson().fromJson(cartItemsJson, type)

        // Populate cart product list
        if (!cartItems.isNullOrEmpty()) {
            showCartProductList.addAll(cartItems)
            showCartProductAdapter.notifyDataSetChanged()
        }

        // Set total price and address
        binding.tvTotalPrice.text = "$productPrice"
        binding.tvAddress.text = initialAddress

        // Close bottom sheet
        binding.ivClose.setOnClickListener {
            dismiss()
        }

        // Address change launcher
        addressLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val newAddress = result.data?.getStringExtra("selected_address")
                val newAddressId = result.data?.getStringExtra("selected_address_id")

                newAddress?.let {
                    binding.tvAddress.text = it
                    binding.tvAddress.tag = newAddressId // Store the dynamic address ID
                }
            }
        }

        // Fetch and display address
        displayAddress(userId)

        binding.tvChangeAddress.setOnClickListener {
            val intent = Intent(requireContext(), SavedAddressActivity::class.java)
            addressLauncher.launch(intent)
        }

        // Place order button
        binding.btnPlaceOrder.setOnClickListener {
            userId.let {
                placeBulkOrder(it)
            }
        }
    }

    private fun placeBulkOrder(userID: String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        if (showCartProductList.isEmpty()) {
            Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val shippingAddressId = binding.tvAddress.tag?.toString() ?: return

        // Make a single API call
        val call = apiServiceInterface.placeOrder(
            placeOrder = "order",
            user_id = userID,
            shipping_address_id = shippingAddressId
        )

        call.enqueue(object : Callback<PlaceOrderModel> {
            override fun onResponse(call: Call<PlaceOrderModel>, response: Response<PlaceOrderModel>) {
                if (response.isSuccessful) {
                    val orderResponse = response.body()
                    if (orderResponse?.status == 200) {
                        Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show()

                        // Clear cart and refresh UI
                        showCartProductList.clear()
                        showCartProductAdapter.notifyDataSetChanged()

                        notifyCartCleared()

                        val intent = Intent(requireContext(), OrderSuccessfullyActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "Order failed: ${orderResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Order failed.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PlaceOrderModel>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun notifyCartCleared() {
        activity?.supportFragmentManager?.setFragmentResult("cartUpdate", Bundle().apply {
            putBoolean("refreshCart", true)
        })
        dismiss()
        clearCart()
    }

    private fun clearCart() {
        showCartProductList.clear()
        showCartProductAdapter.notifyDataSetChanged()
    }


    private fun displayAddress(userId: String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val call = apiServiceInterface.getshippingaddress("get_user_shipping_address", userId)

        call.enqueue(object : Callback<GetAddressResponse> {
            override fun onResponse(call: Call<GetAddressResponse>, response: Response<GetAddressResponse>) {
                if (response.isSuccessful) {
                    val addressResponse = response.body()
                    val addressList = addressResponse?.data?.filterNotNull() ?: emptyList()

                    if (addressList.isNotEmpty()) {
                        val selectedAddress = addressList.first()

                        // Store the address ID dynamically in tag
                        binding.tvAddress.tag = selectedAddress.shippingAddressId

                        val fullAddress = "${selectedAddress.addressLine1}, ${selectedAddress.city}, ${selectedAddress.state}, ${selectedAddress.postalCode}, ${selectedAddress.country}"
                        binding.tvAddress.text = fullAddress
                    }
                }
            }

            override fun onFailure(call: Call<GetAddressResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error fetching address: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
