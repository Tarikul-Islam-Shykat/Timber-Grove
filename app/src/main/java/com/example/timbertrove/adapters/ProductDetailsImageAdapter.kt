package com.example.timbertrove.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.timbertrove.R
import com.example.timbertrove.databinding.SampleProductDetailsImageItemBinding

class ProductDetailsImageAdapter : RecyclerView.Adapter<ProductDetailsImageAdapter.ProductImageViewHolder>() {

    inner class  ProductImageViewHolder(var binding : SampleProductDetailsImageItemBinding) : ViewHolder(binding.root){
        fun bind(imagePath : String){
            Glide
                .with(itemView)
                .load(imagePath)
                .placeholder(R.drawable.placeholder)
                .into(binding.sampelProductdetailsImageView)
        }
    }

    private val diffCallBack = object  : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return  oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return  oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder {
        return  ProductImageViewHolder(
            SampleProductDetailsImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }

}