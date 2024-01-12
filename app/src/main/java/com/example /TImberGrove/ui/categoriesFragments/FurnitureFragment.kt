package com.example.timbertrove.ui.categoriesFragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.timbertrove.data.CategoryInfo
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.viewmodel.FragmentCategoryViewmodel
import com.example.timbertrove.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FurnitureFragment : BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<FragmentCategoryViewmodel>{
        BaseCategoryViewModelFactory(firestore, CategoryInfo.Furniture)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // normal products
        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest { productInfoList ->
                when(productInfoList){
                    is Resource.Loading -> {
                        showOfferLoading()
                    }
                    is Resource.Success -> {
                        productsAdapter.differ.submitList(productInfoList.data)
                        hideOfferLoading()
                    }
                    is Resource.Error -> {
                        hideOfferLoading()
                        Snackbar.make(requireView(), productInfoList.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        // best products
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest { productInfoList ->
                when(productInfoList){
                    is Resource.Loading -> {
                        showBestproductsLoading()
                    }
                    is Resource.Success -> {
                        hideBesrtProductsLoading()
                        bestProductsAdapter.differ.submitList(productInfoList.data)
                    }
                    is Resource.Error -> {
                        hideBesrtProductsLoading()
                        Snackbar.make(requireView(), productInfoList.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

}
