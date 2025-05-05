package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class CategoryData (
    @SerializedName("category_id"    ) var categoryId    : String? = null,
    @SerializedName("category_name"  ) var categoryName  : String? = null,
    @SerializedName("category_image" ) var categoryImage : String? = null,
    @SerializedName("created_at"     ) var createdAt     : String? = null
)
