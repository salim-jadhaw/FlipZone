package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class ProductItems(
    @SerializedName("product_id"    ) var productId    : String? = null,
    @SerializedName("product_name"  ) var productName  : String? = null,
    @SerializedName("product_image" ) var productImage : String? = null,
    @SerializedName("quantity"      ) var quantity     : String? = null,
    @SerializedName("price"         ) var price        : String? = null,

)
