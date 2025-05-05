package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.ModelsClasses.AllProductData
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryData
import com.codewithajaj.ecommerce.databinding.AllProductBinding

class AllProductAdapter(
    private var context: Context,
    private var allProductList: List<AllProductData>
) : RecyclerView.Adapter<AllProductAdapter.AllProductViewHolder>() {

    private var filteredList: MutableList<AllProductData> = allProductList.toMutableList()


    private var onAllProductListener : OnAllProductListener? = null

    interface OnAllProductListener {
        fun onAllProductClick(allProduct : AllProductData)
    }

    fun setUpInterface(onAllProductListener : OnAllProductListener) {
        this.onAllProductListener = onAllProductListener
    }

    fun updateList(newList: List<AllProductData>) {
        filteredList = newList.toMutableList() // Reassign instead of clear() + addAll()
        notifyDataSetChanged()
    }

    fun resetList() {
        filteredList = allProductList.toMutableList() // Restore full list
        notifyDataSetChanged()
    }

    class AllProductViewHolder(var bind: AllProductBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllProductViewHolder {
        val binding = AllProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return AllProductViewHolder(binding)
    }

    override fun getItemCount(): Int = allProductList.size

    override fun onBindViewHolder(holder: AllProductViewHolder, position: Int) {
        val model: AllProductData = allProductList[position]

        holder.bind.apply {
            Glide.with(context)
                .load(model.productImage)
                .placeholder(com.codewithajaj.ecommerce.R.drawable.icon_search)
                .error(com.codewithajaj.ecommerce.R.drawable.icon_shoppingcart)
                .apply(RequestOptions.noTransformation())
                .into(productImage)

            txtTitle.text = model.productName
            txtDiscription.text = model.description
            txtPrice.text = model.productPrice

            root.setOnClickListener {
                onAllProductListener?.onAllProductClick(model)
            }
        }
    }
}