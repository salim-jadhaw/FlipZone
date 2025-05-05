package com.codewithajaj.ecommerce

import com.google.gson.annotations.SerializedName

data class FetchImageModel(
	val message: String? = null,
	val user: User? = null,
	val status: String? = null
)

data class User(
	@SerializedName("user_image") val userImage: String? = null
)

