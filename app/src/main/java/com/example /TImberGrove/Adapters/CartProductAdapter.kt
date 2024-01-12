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
import com.example.timbertrove.data.ProductInfo
import com.example.timbertrove.databinding.SampleCartLayoutBinding
import com.example.timbertrove.databinding.SpecialRvItemBinding
import com.example.timbertrove.helper.getProductPrice

class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.CartProductsViewHolder>() {

    inner class CartProductsViewHolder( val binding : SampleCartLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartProduct: CartProductInfo){
            binding.apply {
                Glide.with(itemView).load(cartProduct.productInfo.images[0]).into(cartImageProduct)
                txtCartProName.text = cartProduct.productInfo.name
                txtNumberOfProduct.text = cartProduct.quantity.toString()

                val priceAfterPercetage = cartProduct.productInfo.offerPercentage.getProductPrice(cartProduct.productInfo.price)//  getProductPrice : Its a helper class for calculating the price
                txtCartProPrice.text = priceAfterPercetage.toString()

                imgProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor?: Color.TRANSPARENT))
                txtProductsize.text = cartProduct.selectedSize?:"".also { imgProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<CartProductInfo>(){
        override fun areItemsTheSame(oldItem: CartProductInfo, newItem: CartProductInfo): Boolean {
            return oldItem.productInfo.id == newItem.productInfo.id
        }
        override fun areContentsTheSame(oldItem: CartProductInfo, newItem: CartProductInfo): Boolean {
            return  oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return  CartProductsViewHolder(
            SampleCartLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)
        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }

        holder.binding.btnAddMoreToCart.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.btnReduceFromCart.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }


    var onProductClick : ((CartProductInfo) -> Unit)? = null
    var onPlusClick : ((CartProductInfo) -> Unit)? = null
    var onMinusClick : ((CartProductInfo) -> Unit)? = null


}
