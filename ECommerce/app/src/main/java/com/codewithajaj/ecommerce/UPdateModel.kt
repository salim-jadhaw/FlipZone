package com.codewithajaj.ecommerce

import com.google.gson.annotations.SerializedName

data class UPdateModel(

	@SerializedName("user_image")
    val userImage: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: String? = null
)

