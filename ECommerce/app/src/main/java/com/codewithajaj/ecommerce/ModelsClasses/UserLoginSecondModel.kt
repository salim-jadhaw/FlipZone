package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class UserLoginSecondModel(
    @SerializedName("user_id"    ) var userId    : String? = null,
    @SerializedName("user_name"  ) var userName  : String? = null,
    @SerializedName("user_email" ) var userEmail : String? = null,
    @SerializedName("user_phone" ) var userPhone : String? = null,
    @SerializedName("user_dob"   ) var userDob   : String? = null
)