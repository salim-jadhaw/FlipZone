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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.codewithajaj.ecommerce.Adapter.CategoryAdapter
import com.codewithajaj.ecommerce.Adapter.SubCategoryAdapter
import com.codewithajaj.ecommerce.ModelsClasses.CategoryData
import com.codewithajaj.ecommerce.ModelsClasses.CategoryModel
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryData
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryModel
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.databinding.ActivityShowAllCategoryBinding
import com.codewithajaj.ecommerce.databinding.ActivitySubCategoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowAllCategoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowAllCategoryBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private var categoryList = mutableListOf<CategoryData>()
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    private var subCategoryList = mutableListOf<SubCategoryData>()
    private var baseURL = "http://192.168.4.211/ecommerce/API/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowAllCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val categoryId = intent.getStringExtra("category_id").toString()

        binding.recyclerViewForCategory.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        fetchCategory()
        categoryAdapter = CategoryAdapter(this, categoryList)
        binding.recyclerViewForCategory.adapter = categoryAdapter



        categoryAdapter.setUpInterface(object : CategoryAdapter.OnCategoryListener {
            override fun onCategoryClick(category: CategoryData) {
                Toast.makeText(this@ShowAllCategoryActivity, "Category : ${category.categoryName}", Toast.LENGTH_SHORT).show()
                category.categoryId?.let { fetchSubCategory(it) }
                binding.tvSelectCategory.visibility = View.GONE
            }
        })

        binding.recyclerViewForSubCategory.layoutManager = GridLayoutManager(this, 2)
        subCategoryAdapter = SubCategoryAdapter(this, subCategoryList)
        binding.recyclerViewForSubCategory.adapter = subCategoryAdapter

        subCategoryAdapter.setUpInterface(object : SubCategoryAdapter.OnSubCategoryListener {
            override fun onSubCategoryClick(subCategory: SubCategoryData) {
                Toast.makeText(this@ShowAllCategoryActivity, "${subCategory.subcategoryName}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ShowAllCategoryActivity, SpecificProductActivity::class.java)
                intent.putExtra("subcategory_id", subCategory.subcategoryId)
                startActivity(intent)
            }

        })

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.searchView.clearFocus()
                val intent = Intent(this@ShowAllCategoryActivity, SearchActivity::class.java)
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

    private fun fetchCategory() {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        binding.loaderForCategory.visibility = View.VISIBLE
        binding.tvSelectCategory.visibility = View.GONE

        val call = apiServiceInterface.getCategory(
            method = "get_categories"
        )

        call.enqueue(object : Callback<CategoryModel> {
            override fun onResponse(call: Call<CategoryModel>, response: Response<CategoryModel>) {
                binding.tvSelectCategory.visibility = View.VISIBLE
                binding.loaderForCategory.visibility = View.GONE

                if (response.isSuccessful) {
                    val categoryModel = response.body()!!

                    if ((categoryModel.status == "200") && categoryModel.categoryData.isNotEmpty()) {
                        categoryList.clear()
                        categoryList.addAll(categoryModel.categoryData)
                        categoryAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@ShowAllCategoryActivity, "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this@ShowAllCategoryActivity,
                        "Failed to load categories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                binding.loaderForCategory.visibility = View.VISIBLE
                binding.tvSelectCategory.visibility = View.GONE
                Toast.makeText(this@ShowAllCategoryActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchSubCategory(categoryId: String) {

        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        binding.loaderForCategory.visibility = View.VISIBLE

        val call = apiServiceInterface.getSubCategory(
            method = "get_subcategory",
            categoryId = categoryId
        )

        Log.d("API_REQUEST", "Fetching subcategories for category_id: $categoryId")

        call.enqueue(object : Callback<SubCategoryModel> {
            override fun onResponse(call: Call<SubCategoryModel>, response: Response<SubCategoryModel>) {

                Log.d("API_RESPONSE", "Raw response: ${response.raw()}")
                binding.loaderForCategory.visibility = View.GONE


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
                            this@ShowAllCategoryActivity,
                            "No Data Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("API_ERROR", "Failed response: ${response.errorBody()?.string()}")
                    Toast.makeText(
                        this@ShowAllCategoryActivity,
                        "Failed to load categories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<SubCategoryModel>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
                binding.loaderForCategory.visibility = View.VISIBLE
                Toast.makeText(this@ShowAllCategoryActivity, "Error fetching data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}