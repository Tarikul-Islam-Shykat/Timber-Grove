package com.example.timbertrove.ui.billing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timbertrove.R
import com.example.timbertrove.adapters.OrderHistoryAdapter
import com.example.timbertrove.databinding.FragmentOrderHistoryBinding
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.viewmodel.OrderHistoryViewHolder
import com.example.timbertrove.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class OrderHistoryFragment : Fragment() {

    private lateinit var binding : FragmentOrderHistoryBinding
    val viewModel by viewModels<OrderHistoryViewHolder>() // name mistake
    val ordersAdapter by lazy { OrderHistoryAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOrderHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUprderHistoryRV()

        // getting all the orders
        lifecycleScope.launchWhenStarted {
            viewModel.allOrders.collectLatest { orderInfo ->
                when(orderInfo){
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        ordersAdapter.differ.submitList(orderInfo.data)
                        if(orderInfo.data.isNullOrEmpty()){
                            binding.tvEmptyOrders.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        ordersAdapter.onClick = { orderInfo ->
            val action = OrderHistoryFragmentDirections.actionOrderHistoryFragmentToOrderDetailsFragment(orderInfo)
            findNavController().navigate(action)
        }
    }

    private fun setUprderHistoryRV() {
        binding.rvAllOrders.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

}