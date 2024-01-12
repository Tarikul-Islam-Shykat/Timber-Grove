package com.example.timbertrove.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.timbertrove.R
import com.example.timbertrove.data.AddressInfo
import com.example.timbertrove.databinding.SampleAddreessLayoutBinding

class BillingAddressAdapter : Adapter<BillingAddressAdapter.AddressViewholder>() {

    var selectedAddress = -1 // fro getting which address is selected

    inner class AddressViewholder(val binding : SampleAddreessLayoutBinding) : ViewHolder(binding.root){
        fun bind(addressInfo: AddressInfo?, isSelected: Boolean) {
            binding.apply {
                buttonAddress.text = addressInfo?.addressTitle
                if(isSelected){
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                }
                else{
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<AddressInfo>(){
        override fun areItemsTheSame(oldItem: AddressInfo, newItem: AddressInfo): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }
        override fun areContentsTheSame(oldItem: AddressInfo, newItem: AddressInfo): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    init { // this is for a particular issue like if you add new address, and upon coming back you see to adress selected. then this function will solve the issure
        differ.addListListener{_,_ ->
            notifyItemChanged(selectedAddress)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewholder {
        return  AddressViewholder(
            SampleAddreessLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: AddressViewholder, position: Int) {
        val addressInfo = differ.currentList[position]
        holder.bind(addressInfo, selectedAddress == position)

        holder.binding.buttonAddress.setOnClickListener {
            if(selectedAddress >= 0 ){
                notifyItemChanged(selectedAddress)
            }
            selectedAddress =  holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(addressInfo)
        }
    }

    var onClick  : ((AddressInfo) -> Unit)? = null
}