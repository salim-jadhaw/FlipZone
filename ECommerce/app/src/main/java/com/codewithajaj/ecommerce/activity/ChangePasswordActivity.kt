package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codewithajaj.ecommerce.ModelsClasses.ChangePasswordModel
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.databinding.ActivityChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChangePasswordBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        binding.changePasswordBtn.setOnClickListener {
            validation()
        }
    }

    private fun validation() {
        val newPassword = binding.newPassWordET.text.toString()
        val confirmPassword = binding.confirmNewPasswordET.text.toString()
        val email = intent.getStringExtra("user_email").toString()

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Password is not entered", Toast.LENGTH_SHORT).show()
            binding.newPassWordET.requestFocus()
            binding.newPassWordET.error = "Please add password"
        } else if (!isValidPassword(password = newPassword)) {
            Toast.makeText(this, "Enter a valid password", Toast.LENGTH_SHORT).show()
            binding.newPassWordET.requestFocus()
            binding.newPassWordET.error =
                "Password must have at least 8 characters, 1 uppercase, 1 lowercase, 1 digit, and 1 special character"
        } else if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show()
            binding.confirmNewPasswordET.requestFocus()
            binding.confirmNewPasswordET.error = "Confirm your password"
        } else if (newPassword != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            binding.confirmNewPasswordET.requestFocus()
            binding.confirmNewPasswordET.error = "Passwords do not match"
        } else {
            changePassword(email, newPassword)
        }
    }

    private fun changePassword(email : String, password: String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val progressDialog = AlertDialog.Builder(this)
            .setView(R.layout.dialog_progress)
            .setCancelable(false)
            .create()
        progressDialog.show()

        val call = apiServiceInterface.changePassword(
            method = "update_password",
            userEmail = email,
            userPassword = password
        )

        call.enqueue(object : Callback<ChangePasswordModel> {
            override fun onResponse(call: Call<ChangePasswordModel>, response: Response<ChangePasswordModel>) {

                if(progressDialog.isShowing) progressDialog.dismiss()

                if (response.isSuccessful) {
                    val changePassword = response.body()!!
                    if (changePassword.status == "200") {
                        Toast.makeText(this@ChangePasswordActivity, "Password changed successfully!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finishAffinity()

                    } else {
                        Toast.makeText(this@ChangePasswordActivity, changePassword.message ?:"password is not change $email", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ChangePasswordActivity, "email is not verified", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChangePasswordModel>, t: Throwable) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this@ChangePasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+$).{8,}$"
        )
        return passwordPattern.matcher(password).matches()
    }
}