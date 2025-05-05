package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class SpecificProductModel(
    @SerializedName("status" ) var status : String?         = null,
    @SerializedName("data"   ) var data   : ArrayList<SpecificProductData> = arrayListOf()
)