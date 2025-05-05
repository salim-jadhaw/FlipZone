package com.codewithajaj.ecommerce.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.codewithajaj.ecommerce.Adapter.ShowCartProductAdapter
import com.codewithajaj.ecommerce.BottomSheetPlaceOrder
import com.codewithajaj.ecommerce.BuyNowBottomSheet
import com.codewithajaj.ecommerce.GetAddressResponse
import com.codewithajaj.ecommerce.ModelsClasses.AddCartQty
import com.codewithajaj.ecommerce.ModelsClasses.CartProductShowData
import com.codewithajaj.ecommerce.ModelsClasses.CartProductShowModel
import com.codewithajaj.ecommerce.ModelsClasses.PlaceOrderModel
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.activity.DashBoardActivity
import com.codewithajaj.ecommerce.databinding.FragmentCartBinding
import okhttp3.internal.notify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {

    private lateinit var binding : FragmentCartBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"
    private var showCartProductList = mutableListOf<CartProductShowData>()
    private lateinit var showCartProductAdapter: ShowCartProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val sharedPreferences = activity?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userID = sharedPreferences?.getString("user_id", null)

        binding.recyclerViewForShowCartProduct.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        showCartProductAdapter = ShowCartProductAdapter(requireContext(), showCartProductList)
        binding.recyclerViewForShowCartProduct.adapter = showCartProductAdapter
        showCartProductAdapter.notifyDataSetChanged()
        if (userID != null) {
            fetchShowCart(userID)
            showCartProductAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show()
        }

        requireActivity().supportFragmentManager.setFragmentResultListener("cartUpdate", viewLifecycleOwner) { _, bundle ->
            val shouldRefresh = bundle.getBoolean("refreshCart", false)
            if (shouldRefresh) {
                val sharedPreferences = activity?.getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
                val userID = sharedPreferences?.getString("user_id", null)
                userID?.let { fetchShowCart(it) } // Refresh the cart
            }
        }

        showCartProductAdapter.setUpInterface(object : ShowCartProductAdapter.OnShowCartProductListener {
            override fun onShowCartProductClick(cartProductShow: CartProductShowData, actionType: String) {
                when (actionType) {
                    "add" -> {
                        Toast.makeText(requireContext(), "Added: ${cartProductShow.productName}", Toast.LENGTH_SHORT).show()
                        userID?.let { cartProductShow.productId?.let { it1 -> addQty(it, it1) } }
                    }
                    "remove" -> {
                        Toast.makeText(requireContext(), "Removed: ${cartProductShow.productName}", Toast.LENGTH_SHORT).show()
                        userID?.let { cartProductShow.productId?.let { it1 -> removeQty(it, it1) } }
                    }
                }
            }
        })

        binding.btnBack.setOnClickListener {
            (activity as? DashBoardActivity)?.findViewById<ViewPager2>(R.id.viewPager2)?.currentItem = 0
        }

        binding.btnOpenBottomSheet.setOnClickListener {
            val sharedPreferences = activity?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val shippingAddress = sharedPreferences?.getString("user_address", "No address available")

            val totalPrice = showCartProductList.sumOf { it.productPrice?.toDouble() ?: 0.0 }.toString()

            val bottomSheet = BottomSheetPlaceOrder.newInstance(shippingAddress.toString(), totalPrice, ArrayList(showCartProductList))
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
    }

    private fun fetchShowCart(userID : String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        binding.noDataLayout.visibility = View.GONE

        val call = apiServiceInterface.getAddToCartProductShow(
            method = "get_cart",
            userID = userID
        )

        call.enqueue(object : Callback<CartProductShowModel> {
            override fun onResponse(
                call: Call<CartProductShowModel>,
                response: Response<CartProductShowModel>
            ) {
                if(response.isSuccessful) {
                    binding.noDataLayout.visibility = View.GONE
                    val cartProductShowModel = response.body()!!
                    if ((cartProductShowModel.status == "200") && cartProductShowModel.data.isNotEmpty()) {
                        showCartProductList.clear()
                        showCartProductList.addAll(cartProductShowModel.data)
                        showCartProductAdapter.notifyDataSetChanged()

                        binding.noDataLayout.visibility = View.GONE

                    } else {
                        binding.noDataLayout.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.noDataLayout.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CartProductShowModel>, t: Throwable) {
                Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addQty(userID: String, productID : String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val call = apiServiceInterface.addCartProductQty(
            method = "add",
            userID = userID,
            productID = productID
        )

        call.enqueue(object : Callback<AddCartQty> {
            override fun onResponse(call: Call<AddCartQty>, response: Response<AddCartQty>) {
                if (response.isSuccessful  && response.body() != null) {
                    val addCartQty = response.body()
                    if (addCartQty?.status == "200") {
                        Toast.makeText(requireContext(), "add", Toast.LENGTH_SHORT).show()
                        val index = showCartProductList.indexOfFirst { it.productId == productID }
                        if (index != -1) {
                            val updatedItem = showCartProductList[index]

                            // Update quantity
                            val newQuantity = updatedItem.quantity.toString().toInt() + 1
                            updatedItem.quantity = newQuantity.toString()

                            // Calculate new price (based on unit price)
                            val unitPrice = updatedItem.productPrice?.toDouble()?.div(newQuantity - 1) ?: 0.0
                            updatedItem.productPrice = (unitPrice * newQuantity).toString()

                            // Refresh the specific item in RecyclerView
                            showCartProductAdapter.notifyItemChanged(index)
                        }

                    } else {
                        Toast.makeText(requireContext(), addCartQty?.message ?:"Failed to add", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AddCartQty>, t: Throwable) {
                Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun removeQty(userID: String, productID : String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val call = apiServiceInterface.addCartProductQty(
            method = "remove",
            userID = userID,
            productID = productID
        )

        call.enqueue(object : Callback<AddCartQty> {
            override fun onResponse(call: Call<AddCartQty>, response: Response<AddCartQty>) {
                if (response.isSuccessful  && response.body() != null) {
                    val addCartQty = response.body()
                    if (addCartQty?.status == "200") {
                        Toast.makeText(requireContext(), "remove", Toast.LENGTH_SHORT).show()
                        val index = showCartProductList.indexOfFirst { it.productId == productID }
                        if (index != -1) {
                            val updatedItem = showCartProductList[index]
                            val newQuantity = updatedItem.quantity.toString().toInt() - 1

                            if (newQuantity <= 0) {
                                // Remove item if quantity is zero
                                showCartProductList.removeAt(index)
                                showCartProductAdapter.notifyItemRemoved(index)
                            } else {
                                // Update quantity
                                updatedItem.quantity = newQuantity.toString()

                                // Calculate updated price
                                val unitPrice = updatedItem.productPrice?.toDouble()?.div(newQuantity + 1) ?: 0.0
                                updatedItem.productPrice = (unitPrice * newQuantity).toString()

                                // Refresh the updated item
                                showCartProductAdapter.notifyItemChanged(index)
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), addCartQty?.message ?:"Failed to add", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AddCartQty>, t: Throwable) {
                Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = activity?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userID = sharedPreferences?.getString("user_id", null)
        if (userID != null) {
            fetchShowCart(userID)
            showCartProductAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show()
        }
    }
}