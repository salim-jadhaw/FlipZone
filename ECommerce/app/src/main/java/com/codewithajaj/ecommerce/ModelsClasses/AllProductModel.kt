package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class AllProductModel(
    @SerializedName("status" ) var status : String?         = null,
    @SerializedName("data"   ) var allProductData   : ArrayList<AllProductData> = arrayListOf()
)
