package com.codewithajaj.ecommerce

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context : Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveUserData(userId: String?, userName: String?, userEmail: String?, userPhone: String?, userDob: String?) {
        editor.putString("user_id", userId)
        editor.putString("user_name", userName)
        editor.putString("user_email", userEmail)
        editor.putString("user_phone", userPhone)
        editor.putString("user_dob", userDob)
        editor.apply()
    }

    fun getUserId(): String? = sharedPreferences.getString("user_id", null)
    fun getUserName(): String? = sharedPreferences.getString("user_name", null)
    fun getUserEmail(): String? = sharedPreferences.getString("user_email", null)
    fun getUserPhone(): String? = sharedPreferences.getString("user_phone", null)
    fun getUserDob(): String? = sharedPreferences.getString("user_dob", null)

    fun setLoginStatus(isLoggedIn: Boolean) {
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun clearPreferences() {
        editor.clear()
        editor.apply()
    }
}