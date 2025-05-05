package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.ModelsClasses.OrdersData
import com.codewithajaj.ecommerce.ModelsClasses.ProductItems
import com.codewithajaj.ecommerce.databinding.ProductOrderHistoryBinding

class OrderHistoryAdapter(
    private var context: Context,
    private var orderHistoryList : List<OrdersData>
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(var bind : ProductOrderHistoryBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = ProductOrderHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return OrderHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = orderHistoryList.size

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = orderHistoryList[position] // CHANGE: Use OrdersData

         holder.bind.showDate.text = order.orderDatetime

         if (order.items.isNotEmpty()) {
            val firstItem = order.items[0]
            holder.bind.apply {
                Glide.with(context)
                    .load(firstItem.productImage)
                    .placeholder(com.codewithajaj.ecommerce.R.drawable.icon_search)
                    .error(com.codewithajaj.ecommerce.R.drawable.icon_shoppingcart)
                    .apply(RequestOptions.centerCropTransform())
                    .into(ivProductImage)

                tvProductName.text = firstItem.productName
                tvTotalAmount.text = firstItem.price
                tvQuantityDigit.text = firstItem.quantity
            }
        }
    }
}