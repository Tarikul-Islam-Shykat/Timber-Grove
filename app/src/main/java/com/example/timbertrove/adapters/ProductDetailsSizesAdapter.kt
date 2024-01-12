package com.example.timbertrove.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.timbertrove.databinding.SampleProductDetailsColorsBinding
import com.example.timbertrove.databinding.SampleProductDetailsSizeBinding

class ProductDetailsSizesAdapter : RecyclerView.Adapter<ProductDetailsSizesAdapter.ProductDetailsSizeViewholder>(){

    private var selecedPostition = -1

    inner class ProductDetailsSizeViewholder(private  var binding : SampleProductDetailsSizeBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(size : String, position : Int){

            binding.sampleTxtSize.text = size // setting the sizes

            if(position == selecedPostition){ // color is selected
                binding.apply {
                    sampleTxtImageShadow.visibility = View.VISIBLE
                }
            }
            else{
                binding.apply {
                    sampleTxtImageShadow.visibility = View.INVISIBLE
                }
            }

        }
    }

    private val differCallBack = object  : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return  oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailsSizeViewholder {
        return ProductDetailsSizeViewholder(
            SampleProductDetailsSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductDetailsSizeViewholder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)

        holder.itemView.setOnClickListener {
            if (selecedPostition >= 0){ // used to updated the selection of color.
                notifyItemChanged(selecedPostition)
            }
            selecedPostition = holder.absoluteAdapterPosition // selected position of color is here.
            notifyItemChanged(selecedPostition)
            onItemClick?.invoke(size)
        }
    }

    var onItemClick : ((String) -> Unit)? = null

}