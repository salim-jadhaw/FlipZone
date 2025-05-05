package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.codewithajaj.ecommerce.Adapter.SpecificProductAdapter
import com.codewithajaj.ecommerce.ModelsClasses.SpecificProductData
import com.codewithajaj.ecommerce.ModelsClasses.SpecificProductModel
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.databinding.ActivitySpecificProductBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SpecificProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpecificProductBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"
    private lateinit var specificProductAdapter: SpecificProductAdapter
    private var specificProductList = mutableListOf<SpecificProductData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpecificProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val subCategoryId = intent.getStringExtra("subcategory_id").toString()

        binding.recyclerViewForSpecificProduct.layoutManager = GridLayoutManager(this@SpecificProductActivity, 2)
        fetchSpecificProduct(subCategoryId)
        specificProductAdapter = SpecificProductAdapter(this, specificProductList)
        binding.recyclerViewForSpecificProduct.adapter = specificProductAdapter

        specificProductAdapter.setUpInterface(object : SpecificProductAdapter.OnSpecificProductListener {
            override fun OnSpecificProductCLick(specificProduct: SpecificProductData) {
                Toast.makeText(this@SpecificProductActivity, "${specificProduct.productName}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SpecificProductActivity, ParticularProductActivity::class.java)
                intent.putExtra("product_id", specificProduct.productId)
                startActivity(intent)
            }
        })

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.searchView.clearFocus()
                val intent = Intent(this@SpecificProductActivity, SearchActivity::class.java)
                startActivity(intent)
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.clearFocus()
    }

    private fun fetchSpecificProduct(subCategoryId: String) {

        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        binding.loader.visibility = View.VISIBLE

        val call = apiServiceInterface.getSpecificProduct(
            method = "get_fix_product",
            subCategoryId = subCategoryId
        )

        call.enqueue(object : Callback<SpecificProductModel> {
            override fun onResponse(call: Call<SpecificProductModel>, response: Response<SpecificProductModel>) {

                binding.loader.visibility = View.GONE

                if (response.isSuccessful) {
                    val specificProductModel = response.body()!!

                    if ((specificProductModel.status == "200") && specificProductModel.data.isNotEmpty()) {
                        specificProductList.clear()
                        specificProductList.addAll(specificProductModel.data)
                        specificProductAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@SpecificProductActivity, "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SpecificProductActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SpecificProductModel>, t: Throwable) {
                binding.loader.visibility = View.VISIBLE
                Toast.makeText(this@SpecificProductActivity, "Error fetching data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}

