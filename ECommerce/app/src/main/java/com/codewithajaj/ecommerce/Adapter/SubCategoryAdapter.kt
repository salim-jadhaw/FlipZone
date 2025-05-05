package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.ModelsClasses.CategoryData
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryData
import com.codewithajaj.ecommerce.databinding.SubCategoryBinding

class SubCategoryAdapter(
    private var context: Context,
    private var subCategoryList: List<SubCategoryData>
) : RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder>(){


    private var onSubCategoryListener : OnSubCategoryListener? = null

    interface OnSubCategoryListener {
        fun onSubCategoryClick(subCategory : SubCategoryData)
    }

    fun setUpInterface(onSubCategoryListener : OnSubCategoryListener) {
        this.onSubCategoryListener = onSubCategoryListener
    }

    class SubCategoryViewHolder(var bind : SubCategoryBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        val binding = SubCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return SubCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = subCategoryList.size

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
        val model : SubCategoryData = subCategoryList[position]

        holder.bind.apply {
            Glide.with(context)
                .load(model.subcategoryImage)
                .placeholder(com.codewithajaj.ecommerce.R.drawable.icon_search)
                .error(com.codewithajaj.ecommerce.R.drawable.icon_shoppingcart)
                .apply(RequestOptions.centerCropTransform())
                .into(subCategoryImageView)

            tvSubCategory.text = model.subcategoryName

            root.setOnClickListener {
                onSubCategoryListener?.onSubCategoryClick(model)
            }
        }
    }

    fun updateData(newList : List<SubCategoryData>) {
        subCategoryList = newList
        notifyDataSetChanged()
    }

}