package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.ModelsClasses.ParticularProductData
import com.codewithajaj.ecommerce.ModelsClasses.SpecificProductData
import com.codewithajaj.ecommerce.databinding.AllProductBinding
import com.codewithajaj.ecommerce.databinding.ParticularProductBinding

class ParticularProductAdapter(
    private var context: Context,
    private var particularProductList: List<ParticularProductData>
) : RecyclerView.Adapter<ParticularProductAdapter.ParticularProductViewHolder>(){

    class ParticularProductViewHolder(var bind : ParticularProductBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticularProductViewHolder {
        val binding = ParticularProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ParticularProductViewHolder(binding)
    }

    override fun getItemCount(): Int = particularProductList.size

    override fun onBindViewHolder(holder: ParticularProductViewHolder, position: Int) {
        val model : ParticularProductData = particularProductList[position]

        holder.bind.apply {
            Glide.with(context)
                .load(model.productImage)
                .placeholder(com.codewithajaj.ecommerce.R.drawable.icon_search)
                .error(com.codewithajaj.ecommerce.R.drawable.icon_shoppingcart)
                .apply(RequestOptions.centerCropTransform())
                .into(productImage)

            tvProductName.text = model.productName
            tvPrice.text = model.productPrice
            tvDescription.text = model.description

        }
    }

}