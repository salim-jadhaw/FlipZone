package com.codewithajaj.ecommerce.activity

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.FetchImageModel
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.UPdateModel
import com.codewithajaj.ecommerce.databinding.ActivityMyProfileBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyProfile : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"
    private val calendar = Calendar.getInstance()
    private var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        checkAndRequestPermissions()

        fetchProfileData()

        binding.txtDOB.setOnClickListener {
            showDatePicker()
        }

        binding.btnProfileUpdate.setOnClickListener {
            updateProfileData()
        }

        binding.backtoAccounttFragment.setOnClickListener {
            finish()
        }

        binding.imgProfile.setOnClickListener {
            showImagePickerDialog()
        }

        fetchProfileImage()
    }

    private fun fetchProfileImage() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId == null) {
            Log.e("Fetch Address", "User ID not found in SharedPreferences")
            return
        }

        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val call = apiServiceInterface.fetchImage(
            method = "get_user",
            userId = userId
        )

        call.enqueue(object : Callback<FetchImageModel> {
            override fun onResponse(call: Call<FetchImageModel>, response: Response<FetchImageModel>) {

                if (response.isSuccessful && response.body() != null) {
                    val fetchImageModel = response.body()!!

                    val imageUrl = fetchImageModel.user?.userImage
                    val fullImageUrl = imageUrl?.let { if (it.startsWith("http")) it else baseURL + it } ?: ""

                    if (fetchImageModel.status == "200" && fullImageUrl.isNotEmpty()) {
                        Glide.with(this@MyProfile)
                            .load(fullImageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_aboutus)
                            .into(binding.imgProfile)
                    } else {
                        binding.imgProfile.setImageResource(R.drawable.ic_aboutus)
                        Toast.makeText(this@MyProfile, "No image found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MyProfile, "Failed to fetch image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FetchImageModel>, t: Throwable) {
                Toast.makeText(this@MyProfile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.CAMERA)
        }

        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray(), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the operation
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.txtDOB.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> checkPermissionsAndOpenCamera()
                1 -> pickImageFromGallery()
            }
        }
        builder.show()
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.imgProfile.setImageURI(it)
            }
        }

    private val captureImageLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success && selectedImageUri != null) {
                binding.imgProfile.setImageURI(selectedImageUri)
            }
        }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun captureImageFromCamera() {
        val photoFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile_photo_${System.currentTimeMillis()}.jpg")
        selectedImageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        captureImageLauncher.launch(selectedImageUri!!)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            captureImageFromCamera()
        } else {
            Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionsAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            captureImageFromCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun fetchProfileData() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        binding.txtProfileName.setText(sharedPreferences.getString("user_name", ""))
        binding.txtProfileEmail.setText(sharedPreferences.getString("user_email", ""))
        binding.tvPhoneNumber.setText(sharedPreferences.getString("user_phone", ""))
        binding.txtDOB.setText(formatDateForDisplay(sharedPreferences.getString("user_dob", "") ?: ""))

        val profileImageUrl = sharedPreferences.getString("user_image", "") ?: ""
        Log.d("ProfileImageUrl", "Profile image URL: $profileImageUrl")

        if (profileImageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(baseURL + profileImageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.icon_account)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgProfile)
        } else {
            Log.d("ProfileImageUrl", "No profile image URL found")
            binding.imgProfile.setImageResource(R.drawable.ic_aboutus)
        }
    }
    private fun updateProfileData() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "") ?: ""
        val userEmail = sharedPreferences.getString("user_email", "") ?: ""
        val editor = sharedPreferences.edit()

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedName = binding.txtProfileName.text.toString().trim()
        val updatedEmail = binding.txtProfileEmail.text.toString().trim()
        val updatedPhone = binding.tvPhoneNumber.text.toString().trim()
        val updatedDob = binding.txtDOB.text.toString().trim()

//        if (selectedImageUri == null) {
//            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val file = getFileFromUri(selectedImageUri!!)
//        if (file == null || !file.exists() || file.length() == 0L) {
//            Toast.makeText(this, "Image file is invalid", Toast.LENGTH_SHORT).show()
//            return
//        }


       if (!updatedName.matches(Regex("^[a-zA-Z ]+\$"))) {
            Toast.makeText(this, "Name should contain only letters", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(updatedEmail).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }
        if (!updatedPhone.matches(Regex("^[0-9]{10}$"))) {
            Toast.makeText(this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
            return
        }


        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val progressDialog = AlertDialog.Builder(this)
            .setView(R.layout.dialog_progress)
            .setCancelable(false)
            .create()
        progressDialog.show()

        val methodBody = "update_user".toRequestBody("text/plain".toMediaTypeOrNull())
        val userIdBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())
        val userEmailBody = updatedEmail.toRequestBody("text/plain".toMediaTypeOrNull())
        val userNameBody = updatedName.toRequestBody("text/plain".toMediaTypeOrNull())
        val userPhoneBody = updatedPhone.toRequestBody("text/plain".toMediaTypeOrNull())
        val userDobBody = formatDateForSaving(updatedDob).toRequestBody("text/plain".toMediaTypeOrNull())
     // val userDobBody = updatedDob.toRequestBody("text/plain".toMediaTypeOrNull())

        val profileImagePart = selectedImageUri?.let { uri ->
            getFileFromUri(uri)?.let { file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("user_image", file.name, requestFile)
            }
        }

        val call = apiServiceInterface.updateProfile(
           methodBody, userIdBody , profileImagePart , userNameBody , userEmailBody ,  userPhoneBody, userDobBody
        )
        call.enqueue(object : Callback<UPdateModel> {
            override fun onResponse(call: Call<UPdateModel>, response: Response<UPdateModel>) {

                if (progressDialog.isShowing) progressDialog.dismiss()

                Toast.makeText(this@MyProfile,response.body()?.message, Toast.LENGTH_SHORT).show()
                editor.putString("user_name", updatedName)
                editor.putString("user_email", updatedEmail)
                editor.putString("user_phone", updatedPhone)
                editor.putString("user_dob", updatedDob)


                selectedImageUri?.let {
                    editor.putString("user_image", it.toString())
                }
                editor.apply()

                fetchProfileImage()
                // fetchProfileData()
            }
            override fun onFailure(call: Call<UPdateModel>, t: Throwable) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this@MyProfile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFileFromUri(uri: Uri): File? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null // No need for context in Activity

        val file = File.createTempFile("profile_image_", ".jpg", cacheDir)

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
    private fun formatDateForDisplay(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            date
        }
    }

    private fun formatDateForSaving(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            date
        }
    }
}


