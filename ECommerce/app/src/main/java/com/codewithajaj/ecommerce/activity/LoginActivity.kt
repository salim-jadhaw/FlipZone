package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.codewithajaj.ecommerce.ModelsClasses.LoginModel
import com.codewithajaj.ecommerce.PreferenceManager
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        preferenceManager = PreferenceManager(this)
        backPressed() 

        binding.signBTN.setOnClickListener{
            validation()
        }

        binding.createACCButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.forgotPasswordTV.setOnClickListener {
            val userEmail = binding.emailET.text.toString().trim()

//            if (userEmail.isEmpty()) {
//                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
//            } else {
                val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
//            }
        }
    }

    private fun validation() {
        val email = binding.emailET.text.toString().trim()
        val password = binding.passwordET.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this@LoginActivity, "email is not Entered", Toast.LENGTH_SHORT)
                .show()
            binding.emailET.requestFocus()
            binding.emailET.error = "please add email"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailET.text.toString()).matches()) {
            Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password is not entered", Toast.LENGTH_SHORT).show()
            binding.passwordET.requestFocus()
            binding.passwordET.error = "Please add password"
        } else {
            loginUser(email, password)
        }
    }
    private fun loginUser(email: String, password: String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val progressDialog = AlertDialog.Builder(this)
            .setView(R.layout.dialog_progress)
            .setCancelable(false)
            .create()
        progressDialog.show()

        val call = apiServiceInterface.loginUser(
            method = "user_login",
            userEmail = email,
            userPassword = password
        )

        call.enqueue(object : Callback<LoginModel> {
            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {

                if(progressDialog.isShowing) progressDialog.dismiss()

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    when (result?.status) {
                        "201" -> {
                            Toast.makeText(
                                this@LoginActivity,
                                result.message ?: "Login Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        "200" -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login Successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val user = result.user

                            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("user_id", user?.userId)
                            editor.putString("user_name", user?.userName)
                            editor.putString("user_email", user?.userEmail)
                            editor.putString("user_phone", user?.userPhone)
                            editor.putString("user_dob", user?.userDob)
                            editor.apply()
                            preferenceManager.setLoginStatus(true)

                            val intent = Intent(this@LoginActivity, DashBoardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        else -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "Unexpected Error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login Failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this@LoginActivity, "Error : ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun backPressed() {
        OnBackPressedDispatcher().addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               finishAffinity()
            }
        })
    }
}
