package com.example.timbertrove.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.timbertrove.databinding.SampleProductDetailsColorsBinding

class ProductDetailsColorAdapter : RecyclerView.Adapter<ProductDetailsColorAdapter.ProductDetailsColorViewholder>(){

    private var selecedPostition = -1

    inner class ProductDetailsColorViewholder(private  var binding : SampleProductDetailsColorsBinding) : ViewHolder(binding.root){
        fun bind(color : Int, position : Int){
            val imageDrawable = ColorDrawable(color)
            binding.sampleImageColor.setImageDrawable(imageDrawable) // setting the color pallette

            if(position == selecedPostition){ // color is selected
                binding.apply {
                    sampleImageShadow.visibility = View.VISIBLE
                    sampleImagePickedIcon.visibility  = View.VISIBLE
                }
            }
            else{
                binding.apply {
                    sampleImageShadow.visibility = View.INVISIBLE
                    sampleImagePickedIcon.visibility  = View.INVISIBLE
                }
            }

        }
    }

    private val differCallBack = object  : DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return  oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailsColorViewholder {
        return ProductDetailsColorViewholder(
            SampleProductDetailsColorsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductDetailsColorViewholder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color, position)

        holder.itemView.setOnClickListener {
            if (selecedPostition >= 0){ // used to updated the selection of color.
                notifyItemChanged(selecedPostition)
            }
            selecedPostition = holder.absoluteAdapterPosition // selected position of color is here.
            notifyItemChanged(selecedPostition)
            onItemClick?.invoke(color)
        }
    }

    var onItemClick : ((Int) -> Unit)? = null

}
