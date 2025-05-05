package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codewithajaj.ecommerce.ModelsClasses.SliderImageModel
import com.codewithajaj.ecommerce.ModelsClasses.SliderImageProduct
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryData
import com.codewithajaj.ecommerce.databinding.CardviewProductBinding

class SliderImageAdapter(
    private var context: Context,
    private var sliderImageList : List<SliderImageProduct>
) : RecyclerView.Adapter<SliderImageAdapter.SliderImageViewHolder>() {

    private var onSliderImageListener : OnSliderImageListener? = null

    interface OnSliderImageListener {
        fun onSliderImageClick(sliderImage : SliderImageProduct)
    }

    fun setUpInterface(onSliderImageListener : OnSliderImageListener) {
        this.onSliderImageListener = onSliderImageListener
    }

    class SliderImageViewHolder(var bind : CardviewProductBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderImageViewHolder {
        val binding = CardviewProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return SliderImageViewHolder(binding)
    }

    override fun getItemCount(): Int = sliderImageList.size

    override fun onBindViewHolder(holder: SliderImageViewHolder, position: Int) {
        val model : SliderImageProduct = sliderImageList[position]

        holder.bind.apply {
            Glide.with(context)
                .load(model.carouselImage)
                .placeholder(com.codewithajaj.ecommerce.R.drawable.icon_search)
                .error(com.codewithajaj.ecommerce.R.drawable.icon_shoppingcart)
                .apply(RequestOptions.centerCropTransform())
                .into(showCardImage)

            root.setOnClickListener{
                onSliderImageListener?.onSliderImageClick(model)
            }
        }
    }
}