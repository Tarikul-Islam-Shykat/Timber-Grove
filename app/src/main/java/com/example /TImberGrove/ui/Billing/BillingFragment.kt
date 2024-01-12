package com.example.timbertrove.ui.billing

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timbertrove.R
import com.example.timbertrove.adapters.BillingAddressAdapter
import com.example.timbertrove.adapters.BillingProductAdapter
import com.example.timbertrove.data.AddressInfo
import com.example.timbertrove.data.CartProductInfo
import com.example.timbertrove.data.ordersData.OrderInfo
import com.example.timbertrove.data.ordersData.OrderStatusInfo
import com.example.timbertrove.databinding.FragmentBillingBinding
import com.example.timbertrove.helper.HorizontaltemDecoration
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.viewmodel.BillingViewModel
import com.example.timbertrove.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding : FragmentBillingBinding
    private val addresssAdapter by lazy { BillingAddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductAdapter ()}
    private val billingViewModel by viewModels<BillingViewModel>()

    // receiving the price from the cart fragment.
    private val args by navArgs<BillingFragmentArgs>()
    private var totalProducts = emptyList<CartProductInfo>()
    private var totalPrice = 0f

    // selected address
    private var selectedAddress : AddressInfo? = null
    private val orderViewModel by viewModels<OrderViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // receive price and prodcuct info from the cart fragment
        totalProducts = args.products.toList()
        totalPrice = args.totalPrice
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setting up recycler view
        setUpWithProductsRv()
        setUpWithAddressRV()

        // collect data from viewmodel
        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest { addressInfoList ->
                when(addressInfoList) {
                    is Resource.Loading ->{
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is  Resource.Success ->{
                        Log.d("billing", addressInfoList.data.toString())
                        addresssAdapter.differ.submitList(addressInfoList.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility  = View.GONE
                        Toast.makeText(requireContext(), "Error in Billing", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // on click on adding address
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        // sending value to adapter
        billingProductsAdapter.differ.submitList(totalProducts)
        binding.tvTotalPrice.text = "$ ${totalPrice}"

        // place order action
         addresssAdapter.onClick = {
             selectedAddress = it
         }
        binding.buttonPlaceOrder.setOnClickListener {
            if(selectedAddress == null){
                Toast.makeText(requireContext(), "Please select and address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }

        // adding loading for placing the order
        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest { orderInfoList ->
                when(orderInfoList) {
                    is Resource.Loading ->{
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is  Resource.Success ->{
                       binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(), orderInfoList.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order Items")
            setMessage("Do you want to order your cart items ?")
            setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes"){ dialog, _ ->
                val order = OrderInfo(
                    OrderStatusInfo.Ordered.statusInfo,
                    totalPrice,
                    totalProducts,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setUpWithAddressRV() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = addresssAdapter
            addItemDecoration(HorizontaltemDecoration())
        }
    }

    private fun setUpWithProductsRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingProductsAdapter
            addItemDecoration(HorizontaltemDecoration())
        }
    }

}
