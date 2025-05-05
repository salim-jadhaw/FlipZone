package com.codewithajaj.ecommerce.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codewithajaj.ecommerce.Adapter.OrderHistoryAdapter
import com.codewithajaj.ecommerce.ModelsClasses.OrderModel
import com.codewithajaj.ecommerce.ModelsClasses.OrdersData
import com.codewithajaj.ecommerce.ModelsClasses.ProductItems
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.databinding.ActivityOrderHistoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrderHistoryBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    // private var orderHistoryList = mutableListOf<ProductItems>()
    private var orderHistoryList = mutableListOf<OrdersData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userID = sharedPreferences?.getString("user_id", null)

        binding.recyclerViewForOrderHistory.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        orderHistoryAdapter = OrderHistoryAdapter(this, orderHistoryList)
        binding.recyclerViewForOrderHistory.adapter = orderHistoryAdapter

        if (userID != null) {
            orderHistory(userID)
        } else {
            Toast.makeText(this@OrderHistoryActivity, "check your cart", Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun orderHistory(userId : String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        binding.loader.visibility = View.VISIBLE

        val call = apiServiceInterface.getOrderHistory(
            method = "get_all_order",
            userID = userId
        )

        call.enqueue(object : Callback<OrderModel> {
            override fun onResponse(call: Call<OrderModel>, response: Response<OrderModel>) {

                binding.loader.visibility = View.GONE

                if(response.isSuccessful) {
                    val orderModel = response.body()!!

                    if (orderModel.status == 200) {
                        orderHistoryList.clear()

                        for (order in orderModel.orders) {
                            orderHistoryList.addAll(orderModel.orders)
                        }

                        orderHistoryAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@OrderHistoryActivity, "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@OrderHistoryActivity, "Failed to load histories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderModel>, t: Throwable) {
                binding.loader.visibility = View.VISIBLE
                Toast.makeText(this@OrderHistoryActivity, "Error fetching data", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }
}