package com.codewithajaj.ecommerce.ModelsClasses

import com.google.gson.annotations.SerializedName

data class OrderModel(
    @SerializedName("message" ) var message : String?           = null,
    @SerializedName("status"  ) var status  : Int?              = null,
    @SerializedName("orders"  ) var orders  : ArrayList<OrdersData> = arrayListOf(),
    @SerializedName("order_datetime") var orderDatetime: String? = null
)
