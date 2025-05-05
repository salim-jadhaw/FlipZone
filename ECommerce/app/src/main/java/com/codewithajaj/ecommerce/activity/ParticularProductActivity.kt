package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.BuyNowBottomSheet
import com.codewithajaj.ecommerce.ModelsClasses.AddCartQty
import com.codewithajaj.ecommerce.ModelsClasses.ParticularProductData
import com.codewithajaj.ecommerce.ModelsClasses.ParticularProductModel
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.ShippingAddressResponse
import com.codewithajaj.ecommerce.databinding.ActivityParticularProductBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParticularProductActivity : AppCompatActivity(){

    private lateinit var binding: ActivityParticularProductBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"
    private  var productData = ParticularProductData()
    private var isProductAddedToCart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticularProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val productId = intent.getStringExtra("product_id").toString()

        fetchParticularProduct(productId)

        binding.btnAddToCart.setOnClickListener{
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val userID = sharedPreferences.getString("user_id", null)
            if (userID != null) {
                addToCart(userID, productId)
            } else {
                Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBuyNow.setOnClickListener {

//            if (!isProductAddedToCart) {
//                Toast.makeText(this, "Please add to cart first", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            val productName = binding.tvProductName.text.toString()
            val productPrice = binding.tvPrice.text.toString()

            val productImage = productData.productImage ?: ""

            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val shippingAddress = sharedPreferences.getString("user_address", "No address available")

            Log.d("ProductImage", "Image URL: $productImage")

            val bottomSheet = BuyNowBottomSheet.newInstance(productName, productPrice, productImage, shippingAddress.toString())
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }

    private fun fetchParticularProduct(productId : String) {

        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        binding.loader.visibility = View.VISIBLE

        val call = apiServiceInterface.getParticularProduct(
            method = "get_particular_product",
            productId = productId
        )

        call.enqueue(object : Callback<ParticularProductModel> {
            override fun onResponse(call: Call<ParticularProductModel>, response: Response<ParticularProductModel>) {

                binding.loader.visibility = View.GONE

                if (response.isSuccessful  && response.body() != null) {
                    val particularProductModel = response.body()
                    if ((particularProductModel?.status == "200") && particularProductModel.data.isNotEmpty()) {
                        productData = particularProductModel.data[0]

                        val imageUrl = if (productData.productImage.toString().startsWith("http")) {
                            productData.productImage
                        } else {
                            "$baseURL${productData.productImage}"
                        }

                        binding.apply {
                            if (!isDestroyed) {
                            Glide.with(this@ParticularProductActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.icon_search)
                                .error(R.drawable.icon_shoppingcart)
                                .apply(RequestOptions.noTransformation())
                                .into(productImage)

                            }

                            tvProductName.text = productData.productName
                            tvPrice.text = productData.productPrice
                            tvDescription.text = productData.description
                        }
                    } else {
                        Toast.makeText(this@ParticularProductActivity, "No Data Found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ParticularProductActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ParticularProductModel>, t: Throwable) {
                binding.loader.visibility = View.VISIBLE
                Toast.makeText(this@ParticularProductActivity, "Error fetching data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun addToCart(userID : String, productId: String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val call = apiServiceInterface.addCartProductQty(
           method = "add",
            userID = userID,
            productID = productId
        )

        call.enqueue(object : Callback<AddCartQty> {
            override fun onResponse(call: Call<AddCartQty>, response: Response<AddCartQty>) {
                if (response.isSuccessful  && response.body() != null) {
                    val cartResponse = response.body()
                    if (cartResponse?.status == "200") {
                        Toast.makeText(this@ParticularProductActivity, "Add to Cart", Toast.LENGTH_SHORT).show()

                        isProductAddedToCart = true

                        val intent = Intent("cart_update")
                        LocalBroadcastManager.getInstance(this@ParticularProductActivity).sendBroadcast(intent)
//                        val result = Bundle()
//                        result.putBoolean("refreshCart", true)
//                        supportFragmentManager.setFragmentResult("cartUpdate", result)
                    } else {
                        Toast.makeText(this@ParticularProductActivity, cartResponse?.message ?:"Failed to add", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<AddCartQty>, t: Throwable) {
                Toast.makeText(this@ParticularProductActivity, "Error fetching data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}