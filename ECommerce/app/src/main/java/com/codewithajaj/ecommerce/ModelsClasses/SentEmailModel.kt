package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

class SentEmailModel(
    @SerializedName("status"  ) var status  : String? = null,
    @SerializedName("message" ) var message : String? = null
)