package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class SubCategoryData(
    @SerializedName("subcategory_id"    ) var subcategoryId    : String? = null,
    @SerializedName("subcategory_name"  ) var subcategoryName  : String? = null,
    @SerializedName("category_id"       ) var categoryId       : String? = null,
    @SerializedName("subcategory_image" ) var subcategoryImage : String? = null,
    @SerializedName("created_at"        ) var createdAt        : String? = null
)
