package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class SliderImageProduct(
    @SerializedName("carousel_image_id" ) var carouselImageId : String? = null,
    @SerializedName("subcategory_id"    ) var subcategoryId   : String? = null,
    @SerializedName("carousel_image"    ) var carouselImage   : String? = null
)
