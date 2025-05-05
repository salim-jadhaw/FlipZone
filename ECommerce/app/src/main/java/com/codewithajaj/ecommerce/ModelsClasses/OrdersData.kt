package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class OrdersData(
    @SerializedName("order_id"       ) var orderId       : String?          = null,
    @SerializedName("total_amount"   ) var totalAmount   : String?          = null,
    @SerializedName("status"         ) var status        : String?          = null,
    @SerializedName("items"          ) var items         : ArrayList<ProductItems> = arrayListOf(),
    @SerializedName("order_datetime" ) var orderDatetime : String?          = null

)
