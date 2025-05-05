package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class CategoryModel(
    @SerializedName("status" ) var status : String? = null,
    @SerializedName("data"   ) var categoryData   : ArrayList<CategoryData> = arrayListOf()
)