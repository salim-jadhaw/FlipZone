package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class SearchProductModel(
    @SerializedName("status"   ) var status   : String?             = null,
    @SerializedName("message"  ) var message  : String?             = null,
    @SerializedName("products" ) var products : ArrayList<SearchProductData> = arrayListOf()
)