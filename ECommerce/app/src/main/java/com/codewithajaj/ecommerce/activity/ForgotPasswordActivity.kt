package com.codewithajaj.ecommerce.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codewithajaj.ecommerce.ModelsClasses.AddCartQty
import com.codewithajaj.ecommerce.ModelsClasses.SentEmailModel
import com.codewithajaj.ecommerce.ModelsClasses.VerifyOtpModel
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.RetrofitClient
import com.codewithajaj.ecommerce.databinding.ActivityForgotPasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private var baseURL = "http://192.168.4.211/ecommerce/API/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        binding.sendButton.setOnClickListener {
            validation()
        }

        binding.verifyOtpBtn.setOnClickListener {
            validationForOtp()
        }

        binding.backtoLoginImageView.setOnClickListener {
            var intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun validation() {
        val email = binding.emailET.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this@ForgotPasswordActivity, "email is not Entered", Toast.LENGTH_SHORT)
                .show()
            binding.emailET.requestFocus()
            binding.emailET.error = "please add email"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailET.text.toString()).matches()) {
            Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show()
        } else{
            sentEmail(email)
        }
    }

    private fun validationForOtp() {
        val email = binding.emailET.text.toString()
        val otp = binding.verifyET.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this@ForgotPasswordActivity, "email is not Entered", Toast.LENGTH_SHORT)
                .show()
            binding.emailET.requestFocus()
            binding.emailET.error = "please add email"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailET.text.toString()).matches()) {
            Toast.makeText(this, "Email not verified", Toast.LENGTH_SHORT).show()
        }  else if (otp.isEmpty()) {
            Toast.makeText(this@ForgotPasswordActivity, "otp is not Entered", Toast.LENGTH_SHORT)
                .show()
            binding.emailET.requestFocus()
            binding.emailET.error = "please enter otp"
        } else {
            verifyOtp(otp, email)
        }
    }

    private fun sentEmail(email : String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val call = apiServiceInterface.sentOtp(
            method = "send_otp",
            email = email
        )

        call.enqueue(object : Callback<SentEmailModel> {
            override fun onResponse(call: Call<SentEmailModel>, response: Response<SentEmailModel>) {
                if (response.isSuccessful) {
                    val sentEmail = response.body()!!
                    if (sentEmail.status == "200") {
                        Toast.makeText(this@ForgotPasswordActivity, "Otp sent on your $email", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, sentEmail.message ?:"email is not sent", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "email is not verified", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<SentEmailModel>, t: Throwable) {
                Toast.makeText(this@ForgotPasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun verifyOtp(otp : String, email: String) {
        val apiServiceInterface = RetrofitClient.getInstance(baseURL)

        val progressDialog = AlertDialog.Builder(this)
            .setView(R.layout.dialog_progress)
            .setCancelable(false)
            .create()
        progressDialog.show()

        val call = apiServiceInterface.verifyOtp(
            method = "verify_otp",
            otp = otp,
            email = email
        )

        call.enqueue(object : Callback<VerifyOtpModel> {
            override fun onResponse(call: Call<VerifyOtpModel>, response: Response<VerifyOtpModel>) {

                if(progressDialog.isShowing) progressDialog.dismiss()

                if (response.isSuccessful) {
                    val verifyOtp = response.body()!!
                    if (verifyOtp.status == "200") {
                        Toast.makeText(this@ForgotPasswordActivity, "Verify Otp", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@ForgotPasswordActivity, ChangePasswordActivity::class.java)
                        intent.putExtra("user_email", email)
                        startActivity(intent)
                        finish()
                    }  else {
                        Toast.makeText(this@ForgotPasswordActivity, verifyOtp.message ?:"otp is not sent on your $email", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "otp is not verified", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<VerifyOtpModel>, t: Throwable) {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this@ForgotPasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }
}