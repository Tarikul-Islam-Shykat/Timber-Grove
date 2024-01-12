package com.example.timbertrove.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.timbertrove.R
import com.example.timbertrove.data.ordersData.OrderInfo
import com.example.timbertrove.data.ordersData.OrderStatusInfo
import com.example.timbertrove.data.ordersData.getOrderStatus
import com.example.timbertrove.databinding.SampleOrderHistoryLayoutBinding


class OrderHistoryAdapter : RecyclerView.Adapter<OrderHistoryAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(private val binding: SampleOrderHistoryLayoutBinding) : ViewHolder(binding.root) {
        fun bind(order: OrderInfo) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date
                val resources = itemView.resources

                val colorDrawable = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatusInfo.Ordered -> {
                        ColorDrawable(resources.getColor(R.color.g_orange_yellow))
                    }
                    is OrderStatusInfo.Confirmed -> {
                        ColorDrawable(resources.getColor(R.color.green))
                    }
                    is OrderStatusInfo.Delivered -> {
                        ColorDrawable(resources.getColor(R.color.green))
                    }
                    is OrderStatusInfo.Shipped -> {
                        ColorDrawable(resources.getColor(R.color.green))
                    }
                    is OrderStatusInfo.Canceled -> {
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                    is OrderStatusInfo.Returned -> {
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                }

                imageOrderState.setImageDrawable(colorDrawable)

            }
        }
    }


    private val diffUtil = object : DiffUtil.ItemCallback<OrderInfo>() {
        override fun areItemsTheSame(oldItem: OrderInfo, newItem: OrderInfo): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: OrderInfo, newItem: OrderInfo): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            SampleOrderHistoryLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((OrderInfo) -> Unit)? = null
}