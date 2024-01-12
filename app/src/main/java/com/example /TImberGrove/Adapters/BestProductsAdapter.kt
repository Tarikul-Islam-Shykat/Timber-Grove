package com.example.timbertrove.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timbertrove.data.ProductInfo
import com.example.timbertrove.databinding.BestDealsRvItemBinding
import com.example.timbertrove.databinding.ProductRvItemBinding


class BestProductsAdapter : RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class BestProductsViewHolder(private val binding : ProductRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(productInfo: ProductInfo){
            binding.apply {
                Glide.with(itemView).load(productInfo.images[0]).into(binding.imgProduct)
                //updating the price
                productInfo.offerPercentage?.let {  value ->
                    val remainPricePercentage = 1f - value
                    val priceAfterOffer = remainPricePercentage * productInfo.price
                    tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                }
                tvName.text = productInfo.name.toString()
                tvPrice.text = productInfo.price.toString()
                tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(
            ProductRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick : ((ProductInfo) -> Unit)? = null
}
