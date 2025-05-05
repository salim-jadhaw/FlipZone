package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class CartModel(
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("status"  ) var status  : Int?    = null
)