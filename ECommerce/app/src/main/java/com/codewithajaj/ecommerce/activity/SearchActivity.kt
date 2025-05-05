package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.codewithajaj.ecommerce.Adapter.AllProductAdapter
import com.codewithajaj.ecommerce.Adapter.SearchAdapter
import com.codewithajaj.ecommerce.ModelsClasses.AllProductData
import com.codewithajaj.ecommerce.ModelsClasses.AllProductModel
import com.codewithajaj.ecommerce.ModelsClasses.SearchProductData
import com.codewithajaj.ecommerce.ModelsClasses.SearchProductModel
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"
    private var searchList = mutableListOf<SearchProductData>()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        binding.recyclerViewForSearchProduct.layoutManager = GridLayoutManager(this, 2)
        searchAdapter = SearchAdapter(this, searchList)
        binding.recyclerViewForSearchProduct.adapter = searchAdapter

        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        searchProduct(it)  // Search when user submits
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        searchProduct(it) // Search as user types
                    } else {
                        clearSearchResults() // Clear results if no input
                    }
                }
                return true
            }
        })


        searchAdapter.setUpInterface(object : SearchAdapter.OnSearchListener {
            override fun onSearchClick(searchProduct: SearchProductData) {
                Toast.makeText(this@SearchActivity, "${searchProduct.productName}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SearchActivity, ParticularProductActivity::class.java)
                intent.putExtra("product_id", searchProduct.productId)
                startActivity(intent)
            }
        })
    }

    private fun searchProduct(query: String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val call = apiServiceInterface.searchProduct(
            search = query
        )

        call.enqueue(object : Callback<SearchProductModel> {
            override fun onResponse(call: Call<SearchProductModel>, response: Response<SearchProductModel>) {
                if (response.isSuccessful) {
                    val searchProduct = response.body()!!
                    if ((searchProduct.status == "200") && searchProduct.products.isNotEmpty()) {
                        searchList.clear()
                        searchList.addAll(searchProduct.products)
                        searchAdapter.notifyDataSetChanged()
                    } else {
                        clearSearchResults()
                        Toast.makeText(this@SearchActivity, "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SearchActivity, "Failed to get all product", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchProductModel>, t: Throwable) {
                Toast.makeText(this@SearchActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearSearchResults() {
        searchList.clear()
        searchAdapter.notifyDataSetChanged()
    }
}