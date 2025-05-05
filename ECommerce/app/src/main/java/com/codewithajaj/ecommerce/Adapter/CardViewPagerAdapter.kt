package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codewithajaj.ecommerce.ModelsClasses.CardImageModel
import com.codewithajaj.ecommerce.databinding.CardviewProductBinding

class CardViewPagerAdapter(
    var context: Context,
    var list: List<CardImageModel>
) : RecyclerView.Adapter<CardViewPagerAdapter.CardViewHolder>() {

    class CardViewHolder(var bind: CardviewProductBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = CardviewProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return CardViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val model : CardImageModel = list[position]

        holder.bind.showCardImage.setImageResource(model.image)
    }
}