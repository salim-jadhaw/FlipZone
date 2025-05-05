package com.codewithajaj.ecommerce.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codewithajaj.ecommerce.ModelsClasses.RegisterModel
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivitySignUpBinding
    private var calender = Calendar.getInstance()
    private var baseURL = "http://192.168.4.211/ecommerce/API/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Date picker for DOB
        binding.dobET.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this@SignUpActivity,
                { _, year, month, dayOfMonth ->
                    // Format the selected date
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)

                    // Set the formatted date to EditText
                    binding.dobET.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Sign-up button click listener
        binding.signUpButton.setOnClickListener {
            validation()
        }

        // Already have an account? Login
        binding.tvAlreadyAccount.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Validate user input
    private fun validation() {
        val fullName = binding.fullNameET.text.toString()
        val email = binding.emailET.text.toString()
        val phone = binding.phoneET.text.toString()
        val birthDate = binding.dobET.text.toString()
        val password = binding.passwordET.text.toString()
        val confirmPassword = binding.confirmPasswordET.text.toString()

        if (fullName.isEmpty()) {
            Toast.makeText(this@SignUpActivity, "Full Name is not Entered", Toast.LENGTH_SHORT).show()
            binding.fullNameET.requestFocus()
            binding.fullNameET.error = "Please add full name"
        } else if (email.isEmpty()) {
            Toast.makeText(this@SignUpActivity, "Email is not Entered", Toast.LENGTH_SHORT).show()
            binding.emailET.requestFocus()
            binding.emailET.error = "Please add email"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show()
        } else if (phone.isEmpty()) {
            Toast.makeText(this@SignUpActivity, "Phone number is not Entered", Toast.LENGTH_SHORT).show()
            binding.phoneET.requestFocus()
            binding.phoneET.error = "Please add phone number"
        } else if (birthDate.isEmpty()) {
            Toast.makeText(this, "Birth date is not entered", Toast.LENGTH_SHORT).show()
            binding.dobET.requestFocus()
            binding.dobET.error = "Please add birth date"
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password is not entered", Toast.LENGTH_SHORT).show()
            binding.passwordET.requestFocus()
            binding.passwordET.error = "Please add password"
        } else if (!isValidPassword(password)) {
            Toast.makeText(this, "Enter a valid password", Toast.LENGTH_SHORT).show()
            binding.passwordET.requestFocus()
            binding.passwordET.error =
                "Password must have at least 8 characters, 1 uppercase, 1 lowercase, 1 digit, and 1 special character"
        } else if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show()
            binding.confirmPasswordET.requestFocus()
            binding.confirmPasswordET.error = "Confirm your password"
        } else if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            binding.confirmPasswordET.requestFocus()
            binding.confirmPasswordET.error = "Passwords do not match"
        } else {
            // Format the date to yyyy-MM-dd before API call
            val formattedDate = formatDateForSaving(birthDate)
            registerApiCalling(fullName, email, phone, formattedDate, password)
        }
    }

    // Register user API call
    private fun registerApiCalling(
        fullName: String,
        email: String,
        phone: String,
        birthDate: String,
        password: String
    ) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val progressDialog = AlertDialog.Builder(this)
            .setView(R.layout.dialog_progress)
            .setCancelable(false)
            .create()
        progressDialog.show()


        val call = apiServiceInterface.registerUser(
            method = "user_registration",
            userName = fullName,
            userEmail = email,
            userPhone = phone,
            userDob = birthDate, // Use the formatted date here
            userPassword = password
        )

        call.enqueue(object : Callback<RegisterModel> {
            override fun onResponse(
                call: Call<RegisterModel>,
                response: retrofit2.Response<RegisterModel>
            ) {
                if (progressDialog.isShowing) progressDialog.dismiss()

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()

                    when (result?.status) {
                        "201" -> {
                            val message = result.message ?: ""

                            if (message.contains("Email already exits") && message.contains("Mobile number already exits")) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Email and Mobile number already exist",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (message.contains("Email already exits")) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Email already exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.emailET.requestFocus()
                                binding.emailET.error = "Email already exists"
                            } else if (message.contains("Mobile number already exits")) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Mobile number already exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.phoneET.requestFocus()
                                binding.phoneET.error = "Mobile number already exists"
                            } else {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Registration Failed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        "200" -> {
                            Log.d("SignUpResponse", "Response: ${response.body()}")
                            Toast.makeText(
                                this@SignUpActivity,
                                result.message ?: "Register Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Go to login
                            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            Toast.makeText(this@SignUpActivity, "${result?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Register failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RegisterModel>, t: Throwable) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this@SignUpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Validate password strength
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+$).{8,}$"
        )
        return passwordPattern.matcher(password).matches()
    }

    // Format date from dd-MM-yyyy to yyyy-MM-dd
    private fun formatDateForSaving(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            date // Return as is if parsing fails
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val birthDate = binding.dobET
        val setDate = month + 1
        val date = "$dayOfMonth/$setDate/$year"
        birthDate.setText(date)
    }
}
