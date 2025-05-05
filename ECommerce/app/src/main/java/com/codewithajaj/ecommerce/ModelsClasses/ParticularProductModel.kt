package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class ParticularProductModel(
    @SerializedName("status" ) var status : String?         = null,
    @SerializedName("data"   ) var data   : ArrayList<ParticularProductData> = arrayListOf()
)
