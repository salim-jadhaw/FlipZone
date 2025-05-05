package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.ModelsClasses.SpecificProductData
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryData
import com.codewithajaj.ecommerce.databinding.AllProductBinding

class SpecificProductAdapter(
    private var context: Context,
    private var specificProductList : List<SpecificProductData>
) : RecyclerView.Adapter<SpecificProductAdapter.SpecificProductViewHolder>() {

    private var onSpecificProductListener : OnSpecificProductListener? = null

    interface OnSpecificProductListener {
        fun OnSpecificProductCLick(specificProduct : SpecificProductData)
    }

    fun setUpInterface(onSpecificProductListener : OnSpecificProductListener) {
        this.onSpecificProductListener = onSpecificProductListener
    }

    class SpecificProductViewHolder(var bind : AllProductBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecificProductViewHolder {
        val binding = AllProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return SpecificProductViewHolder(binding)
    }

    override fun getItemCount(): Int = specificProductList.size

    override fun onBindViewHolder(holder: SpecificProductViewHolder, position: Int) {
        val model : SpecificProductData = specificProductList[position]

        holder.bind.apply {
            Glide.with(context)
                .load(model.productImage)
                .placeholder(com.codewithajaj.ecommerce.R.drawable.icon_search)
                .error(com.codewithajaj.ecommerce.R.drawable.icon_shoppingcart)
                .apply(RequestOptions.centerCropTransform())
                .into(productImage)

            txtTitle.text = model.productName
            txtDiscription.text = model.description
            txtPrice.text = model.productPrice

            root.setOnClickListener {
                root.setOnClickListener {
                    onSpecificProductListener?.OnSpecificProductCLick(model)
                }
            }
        }
    }

}