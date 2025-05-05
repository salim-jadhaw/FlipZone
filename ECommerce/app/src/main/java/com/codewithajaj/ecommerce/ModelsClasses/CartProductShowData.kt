package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class CartProductShowData(
    @SerializedName("cart_id"       ) var cartId       : String? = null,
    @SerializedName("user_id"       ) var userId       : String? = null,
    @SerializedName("product_id"    ) var productId    : String? = null,
    @SerializedName("quantity"      ) var quantity     : String? = null,
    @SerializedName("product_price" ) var productPrice : String? = null,
    @SerializedName("product_name"  ) var productName  : String? = null,
    @SerializedName("product_image" ) var productImage : String? = null,
    @SerializedName("stock"         ) var stock        : String? = null
)
