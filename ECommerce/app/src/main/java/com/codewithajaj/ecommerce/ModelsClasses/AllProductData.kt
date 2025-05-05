package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class AllProductData(
    @SerializedName("product_id"     ) var productId     : String? = null,
    @SerializedName("product_name"   ) var productName   : String? = null,
    @SerializedName("subcategory_id" ) var subcategoryId : String? = null,
    @SerializedName("product_image"  ) var productImage  : String? = null,
    @SerializedName("description"    ) var description   : String? = null,
    @SerializedName("product_price"  ) var productPrice  : String? = null,
    @SerializedName("stock"          ) var stock         : String? = null,
    @SerializedName("created_at"     ) var createdAt     : String? = null
)
