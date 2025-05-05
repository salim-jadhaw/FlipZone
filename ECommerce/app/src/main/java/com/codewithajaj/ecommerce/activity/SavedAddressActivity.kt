package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codewithajaj.ecommerce.Adapter.getShippingAddressAdaptere
import com.codewithajaj.ecommerce.ApiServiceInterface
import com.codewithajaj.ecommerce.BuyNowBottomSheet
import com.codewithajaj.ecommerce.DataItem
import com.codewithajaj.ecommerce.DeletingAddressResponseModel
import com.codewithajaj.ecommerce.GetAddressResponse
import com.codewithajaj.ecommerce.databinding.ActivitySavedAddressBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SavedAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedAddressBinding
    // get address api
    private var baseUrl = "http://192.168.4.211/ecommerce/API/"
    private lateinit var addressAdapter: getShippingAddressAdaptere

    // delete address api
    private var baseUrldlt = "http://192.168.4.211/ecommerce/API/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        binding.backtoAccountFragment.setOnClickListener {
            finish()
        }

        binding.recyclerviewSaveAddress.layoutManager = LinearLayoutManager(this)
        addressAdapter = getShippingAddressAdaptere(this, emptyList()) { address ->
            deleteAddress(address)

        }

        binding.recyclerviewSaveAddress.adapter = addressAdapter

        binding.tvAddNewAddress.setOnClickListener {
            val intent = Intent(this, AddAddressInfo::class.java)
            startActivity(intent)
        }

        addressAdapter.setUpInterface(object : getShippingAddressAdaptere.OnAddressListListener {
            override fun onAddressClick(dataItem: DataItem) {
                Toast.makeText(this@SavedAddressActivity, "${dataItem.shippingAddressId}", Toast.LENGTH_SHORT).show()

                val fullAddress = "${dataItem.addressLine1}, ${dataItem.city}, ${dataItem.state}, ${dataItem.postalCode}"
                val addressId = dataItem.shippingAddressId ?: ""

                val intent = Intent()
                intent.putExtra("selected_address", fullAddress)
                intent.putExtra("selected_address_id", addressId)
                setResult(RESULT_OK, intent)
                finish()
            }

        })
        fetchSavedAddresses()
    }

    private fun fetchSavedAddresses() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId == null) {
            Log.e("Fetch Address", "User ID not found inn SharedPreferences")
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiServiceInterface::class.java)

        binding.loader.visibility = View.VISIBLE

        val call = apiService.getshippingaddress(
            method = "get_user_shipping_address",
            userId = userId
        )

        call.enqueue(object : Callback<GetAddressResponse> {
            override fun onResponse(
                call: Call<GetAddressResponse>,
                response: Response<GetAddressResponse>
            ) {

                binding.loader.visibility = View.GONE

                if (response.isSuccessful) {
                    val addressResponse = response.body()
                    val addressList = addressResponse?.data?.filterNotNull() ?: emptyList()
                    addressAdapter.updateAddressList(addressList)
                    addressAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<GetAddressResponse>, t: Throwable) {
                binding.loader.visibility = View.VISIBLE
                Log.e("API Error", "Failed to fetch addresses: ${t.message}")
            }
        })
    }

    private fun deleteAddress(address: DataItem) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrldlt)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiServiceInterface::class.java)

        val shippingAddressId = address.shippingAddressId ?: run {
            Log.e("Delete Error", "Shipping Address ID is null")
            return
        }


        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null) ?: run {
            Log.e("Delete Error", "User ID not found in SharedPreferences")
            return
        }

        val call = apiService.deleteaddress(
            method = "delete_shipping_address",
            shippingAddressId =  shippingAddressId,
            userId = userId,
        )

        call.enqueue(object : Callback<DeletingAddressResponseModel> {
            override fun onResponse(
                call: Call<DeletingAddressResponseModel>,
                response: Response<DeletingAddressResponseModel>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SavedAddressActivity, "Address deleted successfully", Toast.LENGTH_SHORT).show()
                    fetchSavedAddresses()
                } else {
                    Log.e("Delete Error", "Failed to delete address: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<DeletingAddressResponseModel>, t: Throwable) {
                Log.e("Delete Error", "Failed to delete address: ${t.message}")
            }
        })
    }
}
