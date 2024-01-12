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

class BestDealsProductAdapter : RecyclerView.Adapter<BestDealsProductAdapter.BestDealsProductViewHolder>() {

    inner class BestDealsProductViewHolder(private val binding : BestDealsRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(productInfo: ProductInfo){
            binding.apply {
                Glide.with(itemView).load(productInfo.images[0]).into(binding.imgBestDeal)
                //updating the price
                productInfo.offerPercentage?.let {  value ->
                    val remainPricePercentage = 1f - value
                    val priceAfterOffer = remainPricePercentage * productInfo.price
                    tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                }
                tvDealProductName.text = productInfo.name.toString()
                tvOldPrice.text = productInfo.price.toString()
                tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsProductViewHolder {
        return BestDealsProductViewHolder(
            BestDealsRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }


    var onClick : ((ProductInfo) -> Unit)? = null
}
