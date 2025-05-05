package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.ModelsClasses.AllProductData
import com.codewithajaj.ecommerce.ModelsClasses.SearchProductData
import com.codewithajaj.ecommerce.databinding.AllProductBinding

class SearchAdapter(
    private var context: Context,
    private var searchList: List<SearchProductData>
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var onSearchListener : OnSearchListener? = null

    interface OnSearchListener {
        fun onSearchClick(searchProduct : SearchProductData)
    }

    fun setUpInterface(onSearchListener : OnSearchListener) {
        this.onSearchListener = onSearchListener
    }

    class SearchViewHolder(var bind : AllProductBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = AllProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun getItemCount(): Int = searchList.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val model : SearchProductData = searchList[position]

        holder.bind.apply {
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
                    onSearchListener?.onSearchClick(model)
                }
            }
        }
    }
}