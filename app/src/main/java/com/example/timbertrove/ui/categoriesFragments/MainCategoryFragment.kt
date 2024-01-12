package com.example.timbertrove.ui.categoriesFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timbertrove.R
import com.example.timbertrove.adapters.BestDealsProductAdapter
import com.example.timbertrove.adapters.BestProductsAdapter
import com.example.timbertrove.adapters.SpecialProductsAdapter
import com.example.timbertrove.databinding.FragmentMainBinding
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.utils.hideBottomNavigation
import com.example.timbertrove.utils.showBottomNavigation
import com.example.timbertrove.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class MainCategoryFragment : Fragment() {

    private lateinit var binding : FragmentMainBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsProductAdapter: BestDealsProductAdapter
    private lateinit var bestProductsAdapter : BestProductsAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // special categories
        setUpSpecialProductRecView()
        lifecycleScope.launchWhenStarted {
            viewModel.speacialProducts.collectLatest { productInfoList ->
                when(productInfoList){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(productInfoList.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Error Retrieving  Special products", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
            }
        } }

        // best deal categories
        setUpBestDealsProductRecView()
        lifecycleScope.launchWhenStarted {
            viewModel._bestdealProducts.collectLatest { productInfoList ->
                when(productInfoList){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestDealsProductAdapter.differ.submitList(productInfoList.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Error Retrieving  Special products", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            } }


        // best products
        setUpwithBestProductsRecview()
        lifecycleScope.launchWhenStarted {
            viewModel._bestProducts.collectLatest { productInfoList ->
                when(productInfoList){
                    is Resource.Loading ->{
                        binding.bestProductProgressbar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(productInfoList.data)
                        binding.bestProductProgressbar.visibility = View.GONE

                    }
                    is Resource.Error -> {
                        binding.bestProductProgressbar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error Retrieving  Special products", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        specialProductsAdapter.onClick = { productInfo ->
            val bundle = Bundle().apply { putParcelable("product", productInfo) } // alreadeay buileded  the parsible.
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        bestProductsAdapter.onClick = { productInfo ->
            val bundle = Bundle().apply { putParcelable("product", productInfo) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        bestDealsProductAdapter.onClick = { productInfo ->
            val bundle = Bundle().apply { putParcelable("product", productInfo) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }



        // checking if we are at the bottom of our scroll view and if we are then we need to request for more
        binding.nestedScrollViewMaincategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ view, _, scrollY, _,_ ->
            if(view.getChildAt(0).bottom <= view.height + scrollY){
                viewModel.fetchBestProducts() // so then again we call the fetchBestProducts
            }
        })
    }

    override fun onResume() {
        // hided the bottom nav in the product details. so when got back from product details again need to show the bottom navigation view.
        super.onResume()
        showBottomNavigation() // from utils/showHideBottomNav.kt . custom code for hiding and showing the bottom navigation
    }

    private fun setUpwithBestProductsRecview() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setUpSpecialProductRecView() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }

    private fun setUpBestDealsProductRecView() {
        bestDealsProductAdapter = BestDealsProductAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsProductAdapter
        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressbar.visibility = View.VISIBLE
    }




}