package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codewithajaj.ecommerce.DataItem
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.databinding.ActivityAddAddressInfoBinding
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.ShippingAddressResponse
import com.codewithajaj.ecommerce.UpdateShippingAddressModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAddressInfo : AppCompatActivity() {
    private lateinit var binding: ActivityAddAddressInfoBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"
    private var baseUrlupdate = "http://192.168.4.211/ecommerce/API/"
    private lateinit var sharedPreferences: SharedPreferences
    private var address: DataItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        address = intent.getParcelableExtra("address")
        if (address != null) {
            populateFields(address!!)
        }

        binding.backtoaddressTV.setOnClickListener {
            finish()
        }

        binding.saveaddressBTN.setOnClickListener {
            if (address != null) {
                updateAddress(address!!)
            } else {
                validation()
            }
        }
    }

    private fun populateFields(address: DataItem) {
        binding.fullnameET.setText(address.recipientName)
        binding.phoneNumberET.setText(address.phoneNumber)
        binding.pincodeET.setText(address.postalCode)
        binding.stateeET.setText(address.state)
        binding.cityET.setText(address.city)
        binding.houseNoET.setText(address.addressLine1)
        binding.RoadAreaColonyET.setText(address.addressLine2)
        binding.countryET.setText(address.country)
    }

    private fun validation() {
        val fullName = binding.fullnameET.text.toString().trim()
        val phone = binding.phoneNumberET.text.toString().trim()
        val pincode = binding.pincodeET.text.toString().trim()
        val state = binding.stateeET.text.toString().trim()
        val city = binding.cityET.text.toString().trim()
        val houseAddress = binding.houseNoET.text.toString().trim()
        val areaColony = binding.RoadAreaColonyET.text.toString().trim()
        val country = binding.countryET.text.toString().trim()

        if (fullName.isEmpty()) {
            binding.fullnameET.error = "Please add full name"
            binding.fullnameET.requestFocus()
        } else if (phone.isEmpty()) {
            binding.phoneNumberET.error = "Please add phone number"
            binding.phoneNumberET.requestFocus()
        }
        else if (phone.length != 10) {
            binding.phoneNumberET.error = "Phone number must be exactly 10 digits"
            binding.phoneNumberET.requestFocus()
        }
        else if (pincode.isEmpty() || pincode.length != 6) {
            binding.pincodeET.error = "Please add a valid 6-digit pincode"
            binding.pincodeET.requestFocus()
        }
        else if(country.isEmpty()) {
            binding.countryET.error = "Please add country"
            binding.countryET.requestFocus()
        }
        else if (state.isEmpty()) {
            binding.stateeET.error = "Please add state"
            binding.stateeET.requestFocus()
        } else if (city.isEmpty()) {
            binding.cityET.error = "Please add city"
            binding.cityET.requestFocus()
        } else if (houseAddress.isEmpty()) {
            binding.houseNoET.error = "Please add house number"
            binding.houseNoET.requestFocus()
        } else if (areaColony.isEmpty()) {
            binding.RoadAreaColonyET.error = "Please add area and colony"
            binding.RoadAreaColonyET.requestFocus()
        } else {

            val userId = sharedPreferences.getString("user_id", null)
            if (userId != null) {
                savedShippingAddressApiCalling(userId, fullName, phone, pincode, state, city, houseAddress, areaColony, country)
            } else {
                Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

     private fun savedShippingAddressApiCalling(
        userId: String,
        fullName: String,
        phone: String,
        pincode: String,
        state: String,
        city: String,
        houseNo: String,
        areaColony: String,
        country: String
    ) {
        val apiService = RetrofitClient.getInstance(baseURL)
        val call = apiService.SaveShippingaddress(
            method = "add_shipping_address",
            userId = userId,
            fullName = fullName,
            houseNo = houseNo,
            country = country,
            pincode = pincode,
            state = state,
            city = city,
            areaColony = areaColony,
            phoneNumber = phone
        )

        call.enqueue(object : Callback<ShippingAddressResponse> {
            override fun onResponse(
                call: Call<ShippingAddressResponse>,
                response: Response<ShippingAddressResponse>
            ) {
                if (response.isSuccessful) {
                    val shippingAddressResponse = response.body()!!
                    if (shippingAddressResponse.status == "200") {
                        Toast.makeText(this@AddAddressInfo, "Address saved successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddAddressInfo, "Failed to save address: ${shippingAddressResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AddAddressInfo, "Failed to save address: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ShippingAddressResponse>, t: Throwable) {
                Toast.makeText(this@AddAddressInfo, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateAddress(address: DataItem) {
        val fullName = binding.fullnameET.text.toString().trim()
        val phone = binding.phoneNumberET.text.toString().trim()
        val pincode = binding.pincodeET.text.toString().trim()
        val state = binding.stateeET.text.toString().trim()
        val city = binding.cityET.text.toString().trim()
        val houseaddress = binding.houseNoET.text.toString().trim()
        val areaColony = binding.RoadAreaColonyET.text.toString().trim()
        val country = binding.countryET.text.toString().trim()

        when {
            fullName.isEmpty() -> {
                binding.fullnameET.error = "Please add full name"
                binding.fullnameET.requestFocus()
            }
            phone.isEmpty() -> {
                binding.phoneNumberET.error = "Please add phone number"
                binding.phoneNumberET.requestFocus()
            }
            pincode.isEmpty() || pincode.length < 6 -> {
                binding.pincodeET.error = "Please add a valid 6-digit pincode"
                binding.pincodeET.requestFocus()
            }
            state.isEmpty() -> {
                binding.stateeET.error = "Please add state"
                binding.stateeET.requestFocus()
            }
            city.isEmpty() -> {
                binding.cityET.error = "Please add city"
                binding.cityET.requestFocus()
            }
            country.isEmpty() -> {
                binding.countryET.error = "Please add country"
                binding.countryET.requestFocus()
            }
            houseaddress.isEmpty() -> {
                binding.houseNoET.error = "Please add house number"
                binding.houseNoET.requestFocus()
            }
            areaColony.isEmpty() -> {
                binding.RoadAreaColonyET.error = "Please add area and colony"
                binding.RoadAreaColonyET.requestFocus()
            }
            else -> {
                val userId = sharedPreferences.getString("user_id", "")
                if (userId != null) {
                    updateShippingAddressApiCalling(userId, address.shippingAddressId!!, fullName, phone, pincode, state, city, houseaddress, areaColony, country)
                } else {
                    Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateShippingAddressApiCalling(
        userId: String,
        shippingAddressId: String,
        fullName: String,
        phone: String,
        pincode: String,
        state: String,


        city: String,
        houseNo: String,
        areaColony: String,
        country: String
    ) {
        val apiService = RetrofitClient.getInstance(baseUrlupdate)

        val progressDialog = AlertDialog.Builder(this)
            .setView(R.layout.dialog_progress)
            .setCancelable(false)
            .create()
        progressDialog.show()

        val call = apiService.updateShippingAddress(
            method = "update_shipping_address",
            shippingAddressId = shippingAddressId,
            userId = userId,
            fullname = fullName,
            address1 = houseNo,
            address2 = areaColony,
            city = city,
            state = state,
            pincode = pincode,
            country = country,
            phonenumber = phone
        )

        call.enqueue(object : Callback<UpdateShippingAddressModel> {
            override fun onResponse(
                call: Call<UpdateShippingAddressModel>,
                response: Response<UpdateShippingAddressModel>
            ) {

                if(progressDialog.isShowing) progressDialog.dismiss()

                if (response.isSuccessful) {
                    val updateResponse = response.body()!!
                    if (updateResponse.status == "200") {
                        Toast.makeText(this@AddAddressInfo, "Address updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddAddressInfo, "Failed to update address: ${updateResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AddAddressInfo, "Failed to update address: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateShippingAddressModel>, t: Throwable) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this@AddAddressInfo, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}