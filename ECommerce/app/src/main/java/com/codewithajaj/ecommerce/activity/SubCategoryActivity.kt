package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.codewithajaj.ecommerce.Adapter.CategoryAdapter
import com.codewithajaj.ecommerce.Adapter.SubCategoryAdapter
import com.codewithajaj.ecommerce.ApiServiceInterface
import com.codewithajaj.ecommerce.ModelsClasses.CategoryData
import com.codewithajaj.ecommerce.ModelsClasses.CategoryModel
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryData
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryModel
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.databinding.ActivitySubCategoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubCategoryBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    private var subCategoryList = mutableListOf<SubCategoryData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val categoryId = intent.getStringExtra("category_id").toString()

        binding.recyclerViewForSubCategory.layoutManager = GridLayoutManager(this, 2)
        fetchSubCategory(categoryId)
        subCategoryAdapter = SubCategoryAdapter(this, subCategoryList)
        binding.recyclerViewForSubCategory.adapter = subCategoryAdapter

        binding.tvSubCategory

        subCategoryAdapter.setUpInterface(object : SubCategoryAdapter.OnSubCategoryListener {
            override fun onSubCategoryClick(subCategory: SubCategoryData) {
                Toast.makeText(this@SubCategoryActivity, "${subCategory.subcategoryName}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SubCategoryActivity, SpecificProductActivity::class.java)
                intent.putExtra("subcategory_id", subCategory.subcategoryId)
                startActivity(intent)
            }

        })

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.searchView.clearFocus()
                val intent = Intent(this@SubCategoryActivity, SearchActivity::class.java)
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

    private fun fetchSubCategory(categoryId: String) {

        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        binding.loader.visibility = View.VISIBLE

        val call = apiServiceInterface.getSubCategory(
            method = "get_subcategory",
            categoryId = categoryId
        )

        Log.d("API_REQUEST", "Fetching subcategories for category_id: $categoryId")

        call.enqueue(object : Callback<SubCategoryModel> {
            override fun onResponse(call: Call<SubCategoryModel>, response: Response<SubCategoryModel>) {

                Log.d("API_RESPONSE", "Raw response: ${response.raw()}")
                binding.loader.visibility = View.GONE


                if (response.isSuccessful) {
                    val subCategoryModel = response.body()!!
                    Log.d("API_RESPONSE", "Response Body: $subCategoryModel")

                    Log.d("API_RESPONSE", "Response Body: $subCategoryModel")
                    Log.d("API_RESPONSE", "Raw Response: ${response.body().toString()}")

                    if ((subCategoryModel.status == "200") && subCategoryModel.data.isNotEmpty()) {
                        subCategoryList.clear()
                        subCategoryList.addAll(subCategoryModel.data)
                        subCategoryAdapter.notifyDataSetChanged()

//                        Log.d("API_RESPONSE", "Status: ${categoryModel.status}")
//                        Log.d("API_RESPONSE", "Category Data Size: ${categoryModel.categoryData.size}")
//
//                        categoryAdapter =
//                            CategoryAdapter(requireContext(), categoryModel.categoryData)
//                        binding.recyclerViewForCategory.adapter = categoryAdapter
                    } else {
                        Log.e("API_RESPONSE", "No data received")
                        Toast.makeText(
                            this@SubCategoryActivity,
                            "No Data Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("API_ERROR", "Failed response: ${response.errorBody()?.string()}")
                    Toast.makeText(
                        this@SubCategoryActivity,
                        "Failed to load categories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<SubCategoryModel>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
                binding.loader.visibility = View.VISIBLE
                Toast.makeText(this@SubCategoryActivity, "Error fetching data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}