package com.codewithajaj.ecommerce.fragments

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.codewithajaj.ecommerce.Adapter.AllProductAdapter
import com.codewithajaj.ecommerce.Adapter.CategoryAdapter
import com.codewithajaj.ecommerce.Adapter.SliderImageAdapter
import com.codewithajaj.ecommerce.ModelsClasses.AllProductData
import com.codewithajaj.ecommerce.ModelsClasses.AllProductModel
import com.codewithajaj.ecommerce.ModelsClasses.CategoryData
import com.codewithajaj.ecommerce.ModelsClasses.CategoryModel
import com.codewithajaj.ecommerce.ModelsClasses.SliderImageModel
import com.codewithajaj.ecommerce.ModelsClasses.SliderImageProduct
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.activity.DashBoardActivity
import com.codewithajaj.ecommerce.activity.ParticularProductActivity
import com.codewithajaj.ecommerce.activity.SearchActivity
import com.codewithajaj.ecommerce.activity.ShowAllCategoryActivity
import com.codewithajaj.ecommerce.activity.SpecificProductActivity
import com.codewithajaj.ecommerce.activity.SubCategoryActivity
import com.codewithajaj.ecommerce.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private var baseURL = "http://192.168.4.211/ecommerce/API/"
    private var baseURLForAllProduct = "http://192.168.4.211/ecommerce/API/"
    private var categoryList = mutableListOf<CategoryData>()
    private var allProductList = mutableListOf<AllProductData>()
    private lateinit var allProductAdapter: AllProductAdapter
    private var sliderImageList = mutableListOf<SliderImageProduct>()
    private lateinit var sliderImageAdapter: SliderImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

//        var list = listOf<CardImageModel>(
//            CardImageModel(R.drawable.iamge),
//            CardImageModel(R.drawable.iamge)
//        )
//
//        binding.recyclerViewForCard.layoutManager =
//            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
//        cardViewPagerAdapter = CardViewPagerAdapter(requireContext(), list)
//        binding.recyclerViewForCard.adapter = cardViewPagerAdapter


        binding.recyclerViewForCard.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        sliderImageAdapter = SliderImageAdapter(requireContext(), sliderImageList)
        binding.recyclerViewForCard.adapter = sliderImageAdapter
        fetchCarousel()


        sliderImageAdapter.setUpInterface(object : SliderImageAdapter.OnSliderImageListener {
            override fun onSliderImageClick(sliderImage: SliderImageProduct) {
                Toast.makeText(requireContext(), "${sliderImage.subcategoryId}", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), SpecificProductActivity::class.java)
                intent.putExtra("subcategory_id", sliderImage.subcategoryId)
                startActivity(intent)
            }
        })

        binding.profileImageView.setOnClickListener {
            (activity as? DashBoardActivity)?.findViewById<ViewPager2>(R.id.viewPager2)?.currentItem = 2
        }


        binding.recyclerViewForCategory.layoutManager = GridLayoutManager(requireContext(), 5)
        fetchCategory()
        categoryAdapter = CategoryAdapter(requireContext(), categoryList)
        binding.recyclerViewForCategory.adapter = categoryAdapter


        categoryAdapter.setUpInterface(object : CategoryAdapter.OnCategoryListener {
            override fun onCategoryClick(category: CategoryData) {
                Toast.makeText(requireContext(), "Category : ${category.categoryName}", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), SubCategoryActivity::class.java)
                intent.putExtra("category_id", category.categoryId)
                Log.d("CATEGORY_CLICKED", "Clicked Category ID: ${category.categoryId}")
                startActivity(intent)
            }
        })

        binding.showMore.setOnClickListener {
            startActivity(Intent(requireContext(), ShowAllCategoryActivity::class.java))
        }

        binding.recyclerViewForAllProduct.layoutManager = GridLayoutManager(requireContext(), 2)
        allProductAdapter = AllProductAdapter(requireContext(), allProductList)
        binding.recyclerViewForAllProduct.adapter = allProductAdapter
        fetchAllProduct()

        allProductAdapter.setUpInterface(object : AllProductAdapter.OnAllProductListener {
            override fun onAllProductClick(allProduct: AllProductData) {
                Toast.makeText(requireContext(), "${allProduct.productName}", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), ParticularProductActivity::class.java)
                intent.putExtra("product_id", allProduct.productId)
                startActivity(intent)
            }
        })

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.searchView.clearFocus()
                val intent = Intent(requireContext(), SearchActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.clearFocus()
    }

    private fun fetchCategory() {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        binding.loaderForCategory.visibility = View.VISIBLE

        val call = apiServiceInterface.getCategory(
            method = "get_categories"
        )

        call.enqueue(object : Callback<CategoryModel> {
            override fun onResponse(call: Call<CategoryModel>, response: Response<CategoryModel>) {
                binding.loaderForCategory.visibility = View.GONE

                if (response.isSuccessful) {
                    val categoryModel = response.body()!!

                    if ((categoryModel.status == "200") && categoryModel.categoryData.isNotEmpty()) {
                        categoryList.clear()
                        categoryList.addAll(categoryModel.categoryData.take(5))
                        categoryAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load categories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                binding.loaderForCategory.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchAllProduct() {
        val apiServiceInterface = RetrofitClient.getInstance(baseURLForAllProduct)

        binding.loaderForAllProduct.visibility = View.VISIBLE

        val call = apiServiceInterface.getAllProduct(
            method = "get_product"
        )

        call.enqueue(object : Callback<AllProductModel> {
            override fun onResponse(
                call: Call<AllProductModel>,
                response: Response<AllProductModel>
            ) {
                binding.loaderForAllProduct.visibility = View.GONE

                if (response.isSuccessful) {
                    val allProductModel = response.body()!!

                    if ((allProductModel.status == "200") && allProductModel.allProductData.isNotEmpty()) {
                        allProductList.clear()
                        allProductList.addAll(allProductModel.allProductData)
                        allProductAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to get all product",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AllProductModel>, t: Throwable) {
                binding.loaderForAllProduct.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchCarousel() {
        val apiService = RetrofitClient.getInstance(baseURL)
        val call = apiService.sliderImage(
            method = "get_all_carousel_images",
        )

        call.enqueue(object : Callback<SliderImageModel> {
            override fun onResponse(call: Call<SliderImageModel>, response: Response<SliderImageModel>) {
                if (response.isSuccessful && response.body() != null) {
                    val sliderResponse = response.body()!!
                    if ((sliderResponse.status == 200)) {
                        sliderImageList.clear()
                        sliderImageList.addAll(sliderResponse.carouselImages)
                        sliderImageAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<SliderImageModel>, t: Throwable) {
                Log.e("API_ERROR", "Error fetching carousel: ${t.message}")
            }
        })
    }
}
