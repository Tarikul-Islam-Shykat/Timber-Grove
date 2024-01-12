package com.example.timbertrove.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timbertrove.data.ProductInfo
import com.example.timbertrove.databinding.SpecialRvItemBinding

class SpecialProductsAdapter: RecyclerView.Adapter<SpecialProductsAdapter.SpecialProductsViewHolder>() {

    inner class SpecialProductsViewHolder(private val binding : SpecialRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(productInfo: ProductInfo){
            binding.apply {
                Glide.with(itemView).load(productInfo.images[0]).into(binding.speacialProductImgAd)
                tvSpecialProductPrice.text = productInfo.price.toString()
                tvSpecialProductName.text = productInfo.name
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<ProductInfo>(){
        override fun areItemsTheSame(oldItem: ProductInfo, newItem: ProductInfo): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: ProductInfo, newItem: ProductInfo): Boolean {
            return  oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {
        return  SpecialProductsViewHolder(
            SpecialRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }


    var onClick : ((ProductInfo) -> Unit)? = null

}