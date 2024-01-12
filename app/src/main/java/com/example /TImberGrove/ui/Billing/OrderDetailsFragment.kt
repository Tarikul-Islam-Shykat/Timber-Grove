package com.example.timbertrove.ui.billing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timbertrove.R
import com.example.timbertrove.adapters.BillingProductAdapter
import com.example.timbertrove.data.ordersData.OrderStatusInfo
import com.example.timbertrove.data.ordersData.getOrderStatus
import com.example.timbertrove.databinding.FragmentOrderDetailsBinding
import com.example.timbertrove.helper.VerticalItemDecoration
import com.google.api.Distribution.BucketOptions.Linear

class OrderDetailsFragment : Fragment() {

    private lateinit var binding : FragmentOrderDetailsBinding
    private  val billingProductsAdatper by lazy { BillingProductAdapter() }
    private val args by navArgs<OrderDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOrderDetailsBinding.inflate(inflater)
        return inflater.inflate(R.layout.fragment_order_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpOrderRv()
        val order = args.orderDetails
        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"

            stepView.setSteps(
                mutableListOf(
                    OrderStatusInfo.Ordered.statusInfo,
                    OrderStatusInfo.Confirmed.statusInfo,
                    OrderStatusInfo.Shipped.statusInfo,
                    OrderStatusInfo.Delivered.statusInfo,
                    )
            )

            val currentOrderState  = when(getOrderStatus( order.orderStatus)){
                is OrderStatusInfo.Ordered -> 0
                is OrderStatusInfo.Confirmed -> 1
                is OrderStatusInfo.Shipped -> 2
                is OrderStatusInfo.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderState, false)
                if(currentOrderState == 3){
                    stepView.done(true)
                }


            tvFullName.text = order.addreess.fullName
            tvAddress.text = "${order.addreess.street} ${order.addreess.city}"
            tvPhoneNumber.text = order.addreess.phone
            tvTotalPrice.text = "$ ${order.totalPrice.toString()}"
        }

        billingProductsAdatper.differ.submitList(order.products)
    }

    private fun setUpOrderRv() {
       binding.rvProducts.apply {
           adapter = billingProductsAdatper
           layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
           addItemDecoration(VerticalItemDecoration())
       }
    }


}
