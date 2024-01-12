package com.example.timbertrove.ui.productDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timbertrove.R
import com.example.timbertrove.adapters.ProductDetailsColorAdapter
import com.example.timbertrove.adapters.ProductDetailsImageAdapter
import com.example.timbertrove.adapters.ProductDetailsSizesAdapter
import com.example.timbertrove.data.CartProductInfo
import com.example.timbertrove.databinding.FragmentProductDetailsBinding
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.utils.hideBottomNavigation
import com.example.timbertrove.viewmodel.ProductDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>() // this for transferring the data from other fragments. marked in the nav graph
    private lateinit var binding : FragmentProductDetailsBinding

    private val imageAdapter by lazy { ProductDetailsImageAdapter() }
    private val sizesAdapter by lazy { ProductDetailsSizesAdapter() }
    private val colorAdapter by lazy { ProductDetailsColorAdapter() }

    private var selectedColor : Int? = null
    private var selectedSize : String? = null
    private val viewModel by viewModels<ProductDetailsViewModel>() //  this is the only view model for handling the cart. and also for the product deals

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        hideBottomNavigation() // from utils/showHideBottomNav.kt . custom code for hiding and showing the bottom navigation

        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product // got the whole whole product info here from other fragments.

        setUpSizesRv()
        setupColorRV()
        setUpImageViewPager()

        binding.apply {
            txtProductDetailsName.text = product.name
            txtProductDetailsPrice.text = "$ ${product.price}"
            txtProductDetailsDescription.text = product.description

            // if no colors found
            if (product.colors.isNullOrEmpty())
                txtColor.visibility = View.INVISIBLE

            // if no sizes found
            if (product.sizes.isNullOrEmpty())
                txtSize.visibility = View.INVISIBLE
        }

        imageAdapter.differ.submitList(product.images)
        product.colors?.let {  // colors can be null. so null check
            colorAdapter.differ.submitList(it)
        }
        product.sizes?.let {  // sizes can be null to.
            sizesAdapter.differ.submitList(it)
        }

        // saving the color and sizes
        sizesAdapter.onItemClick = {
            selectedSize = it
        }
        colorAdapter.onItemClick ={
            selectedColor = it
        }

        // action on add to cart button
        binding.btnAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProductInfo(product, 1, selectedColor, selectedSize))
        }
        // handing the action
        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){

                    is  Resource.Loading -> {
                        binding.btnAddToCart.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnAddToCart.revertAnimation()
                        binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.green))
                    }

                    is Resource.Error -> {
                        binding.btnAddToCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


    }


    private fun setUpImageViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = imageAdapter
        }
    }

    private fun setupColorRV() {
        binding.rvColors.apply {
            adapter = colorAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setUpSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

}