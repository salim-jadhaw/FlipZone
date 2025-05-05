package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.ModelsClasses.CategoryData
import com.codewithajaj.ecommerce.databinding.MainCategoryBinding
import com.codewithajaj.ecommerce.fragments.HomeFragment

class CategoryAdapter(
    private var context: Context,
    private var categoryList: List<CategoryData>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var onCategoryListener : OnCategoryListener? = null

    interface OnCategoryListener {
        fun onCategoryClick(category : CategoryData)
    }

    fun setUpInterface(onCategoryListener : OnCategoryListener) {
        this.onCategoryListener = onCategoryListener
    }

    class CategoryViewHolder(var bind : MainCategoryBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = MainCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val model : CategoryData = categoryList[position]

        holder.bind.apply {
            tvCategory.text = model.categoryName

//            Glide.with(context)
//                .load(model.categoryImage)
//                .placeholder(com.codewithajaj.ecommerce.R.drawable.icon_search)
//                .error(com.codewithajaj.ecommerce.R.drawable.icon_shoppingcart)
//                .into(categoryImage)


            Glide.with(context)
                .load(model.categoryImage)
                .placeholder(com.codewithajaj.ecommerce.R.drawable.icon_search)
                .error(com.codewithajaj.ecommerce.R.drawable.icon_shoppingcart)
                .apply(RequestOptions.circleCropTransform())
                .into(categoryImage)

            root.setOnClickListener {
                onCategoryListener?.onCategoryClick(model)
            }
        }
    }

    fun updateData(newList: List<CategoryData>) {
        categoryList = newList
        notifyDataSetChanged()
    }
}