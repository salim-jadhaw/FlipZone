package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class SliderImageModel(
    @SerializedName("message"         ) var message        : String?                   = null,
    @SerializedName("status"          ) var status         : Int?                      = null,
    @SerializedName("carousel_images" ) var carouselImages : ArrayList<SliderImageProduct> = arrayListOf()
)
