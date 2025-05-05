package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.ModelsClasses.CartProductShowData
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryData
import com.codewithajaj.ecommerce.databinding.ShowCartProductBinding

class ShowCartProductAdapter(
    private var context: Context,
    private var showCartProductList: MutableList<CartProductShowData>
) : RecyclerView.Adapter<ShowCartProductAdapter.ShowCartProductViewHolder>(){


    private var onShowCartProductListener : OnShowCartProductListener? = null

    interface OnShowCartProductListener {
        fun onShowCartProductClick(cartProductShow : CartProductShowData, actionType: String)
    }

    fun setUpInterface(onShowCartProductListener : OnShowCartProductListener) {
        this.onShowCartProductListener = onShowCartProductListener
    }

    class ShowCartProductViewHolder(var bind : ShowCartProductBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowCartProductViewHolder {
        val binding = ShowCartProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ShowCartProductViewHolder(binding)
    }

    override fun getItemCount(): Int = showCartProductList.size

    override fun onBindViewHolder(holder: ShowCartProductViewHolder, position: Int) {
        val model : CartProductShowData = showCartProductList[position]

        holder.bind.apply {
            Glide.with(context)
                .load(model.productImage)
                .placeholder(com.codewithajaj.ecommerce.R.drawable.icon_search)
                .error(com.codewithajaj.ecommerce.R.drawable.icon_shoppingcart)
                .apply(RequestOptions.noTransformation())
                .into(productImage)

            productTitle.text = model.productName
            productPrice.text = model.productPrice
            tvQuantity.text = model.quantity

            btnAddQty.setOnClickListener {
                onShowCartProductListener?.onShowCartProductClick(model, "add")
            }

            btnRemoveQty.setOnClickListener {
                onShowCartProductListener?.onShowCartProductClick(model, "remove")
            }
        }
    }

    fun updateList(newList : MutableList<CartProductShowData>) {
        showCartProductList.clear()
        showCartProductList.addAll(newList)
        notifyDataSetChanged()

    }
}