package com.example.timbertrove.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timbertrove.data.CartProductInfo
import com.example.timbertrove.databinding.SampleBillingProductsLayoutBinding
import com.example.timbertrove.helper.getProductPrice

class BillingProductAdapter :
    RecyclerView.Adapter<BillingProductAdapter.BillingProductViewholder>() {

    inner class BillingProductViewholder(val binding: SampleBillingProductsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(billingProduct: CartProductInfo) {
            binding.apply {
                Glide.with(itemView).load(billingProduct.productInfo.images[0])
                    .into(imageCartProduct)
                tvProductCartName.text = billingProduct.productInfo.name
                tvBillingProductQuantity.text = billingProduct.quantity.toString()

                val priceAfterPercetage =
                    billingProduct.productInfo.offerPercentage.getProductPrice(billingProduct.productInfo.price)//  getProductPrice : Its a helper class for calculating the price
                tvProductCartPrice.text = priceAfterPercetage.toString()

                imageCartProductColor.setImageDrawable(
                    ColorDrawable(
                        billingProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                tvCartProductSize.text = billingProduct.selectedSize ?: "".also {
                    imageCartProductSize.setImageDrawable(
                        ColorDrawable(Color.TRANSPARENT)
                    )
                }
            }

        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<CartProductInfo>() {
        override fun areItemsTheSame(oldItem: CartProductInfo, newItem: CartProductInfo): Boolean {
            return oldItem.productInfo == newItem.productInfo
        }

        override fun areContentsTheSame(
            oldItem: CartProductInfo,
            newItem: CartProductInfo
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductViewholder {
        return BillingProductViewholder(
            SampleBillingProductsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: BillingProductViewholder, position: Int) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)
    }

}
