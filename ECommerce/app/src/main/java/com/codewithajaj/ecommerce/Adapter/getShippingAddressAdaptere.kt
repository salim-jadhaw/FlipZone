package com.codewithajaj.ecommerce.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codewithajaj.ecommerce.Adapter.SubCategoryAdapter.SubCategoryViewHolder
import com.codewithajaj.ecommerce.DataItem
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryData
import com.codewithajaj.ecommerce.R
import com.codewithajaj.ecommerce.activity.AddAddressInfo
import com.codewithajaj.ecommerce.databinding.ItemAddressdisplayBinding
import com.codewithajaj.ecommerce.databinding.SubCategoryBinding

class getShippingAddressAdaptere(private var context: Context, private var addressList: List<DataItem>,private val onDeleteClickListener: (DataItem) -> Unit )
    : RecyclerView.Adapter<getShippingAddressAdaptere.ViewHolder>() {


    private var onAddressListListener : OnAddressListListener? = null

    interface OnAddressListListener {
        fun onAddressClick(dataItem : DataItem)
    }

    fun setUpInterface(onAddressListListener : OnAddressListListener) {
        this.onAddressListListener = onAddressListListener
    }

    class ViewHolder(var bind : ItemAddressdisplayBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddressdisplayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addressList[position]

        holder.bind.nameTV.text = address.recipientName ?: "N/A"

        holder.bind.address1.text = address.addressLine1 ?: "N/A"
        holder.bind.address2.text = address.addressLine2 ?: "N/A"
        holder.bind.cityTV.text = address.city ?: "N/A"
        holder.bind.stateTV.text = address.state ?: "N/A"
        holder.bind.countyTV.text = address.country ?: "N/A"
        holder.bind.pincodeTV.text = address.postalCode ?: "N/A"
        holder.bind.MoNumberTV.text = address.phoneNumber ?: "N/A"

        holder.bind.deleteAddressTV.setOnClickListener {
            onDeleteClickListener(address)
        }

        holder.bind.editAddressTV.setOnClickListener {
            val intent = Intent(context, AddAddressInfo::class.java).apply {
                putExtra("address", address)
            }
            context.startActivity(intent)
        }

        holder.bind.root.setOnClickListener {
            onAddressListListener?.onAddressClick(address)
        }
    }

    override fun getItemCount(): Int = addressList.size

    fun updateAddressList(newList: List<DataItem>) {
        addressList = newList
        notifyDataSetChanged()
    }
}
