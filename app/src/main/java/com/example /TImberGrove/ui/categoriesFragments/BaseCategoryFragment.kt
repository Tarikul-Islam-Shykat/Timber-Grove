package com.example.timbertrove.ui.categoriesFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timbertrove.R
import com.example.timbertrove.adapters.BestProductsAdapter
import com.example.timbertrove.databinding.FragmentBaseCategoryBinding
import com.example.timbertrove.utils.showBottomNavigation


open class BaseCategoryFragment : Fragment() {

    private lateinit var binding : FragmentBaseCategoryBinding
    // by lazy means we intialize it when we call it for the first time
    protected val bestProductsAdapter : BestProductsAdapter by lazy { BestProductsAdapter() } // need to access from other categories too
    protected val productsAdapter : BestProductsAdapter by lazy { BestProductsAdapter() } // need to access from other categories too

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpProductRecylerView()
        setUPBestProductRecview()

        // request more product when at end  horizontally
        binding.rvBaseCateogryProducts.addOnScrollListener(object  : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollHorizontally(1) && dx != 0){
                    onProductPagingRequest()
                }
            }
        })

        // this is for requesting more products when vertically
        binding.nestedScrollViewBasecategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ view, _, scrollY, _, _ ->
            if(view.getChildAt(0).bottom <= view.height+scrollY){
                onBestProductsPagingRequest()
            }
        })

        // for going to the product details fragment
        bestProductsAdapter.onClick = { productInfo ->
            val bundle = Bundle().apply { putParcelable("product", productInfo) } // alreadeay buileded  the parsible.
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        productsAdapter.onClick = { productInfo ->
            val bundle = Bundle().apply { putParcelable("product", productInfo) } // alreadeay buileded  the parsible.
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }


    }

    override fun onResume() {
        // hided the bottom nav in the product details. so when got back from product details again need to show the bottom navigation view.
        super.onResume()
        showBottomNavigation() // from utils/showHideBottomNav.kt . custom code for hiding and showing the bottom navigation
    }


    fun showOfferLoading(){
        binding.baseCategoryProgressbar.visibility = View.VISIBLE
    }

    fun hideOfferLoading(){
        binding.baseCategoryProgressbar.visibility = View.GONE
    }

    fun showBestproductsLoading(){
        binding.baseCatBestProductsProbar.visibility = View.VISIBLE
    }

    fun hideBesrtProductsLoading(){
        binding.baseCatBestProductsProbar.visibility = View.GONE
    }

    open  fun onProductPagingRequest(){

    }

    open fun onBestProductsPagingRequest(){

    }

    private fun setUpProductRecylerView() {
        binding.rvBaseCateogryProducts.apply {
            binding.rvBaseCateogryProducts.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = productsAdapter
            }
        }

    }
    private fun setUPBestProductRecview() {
        binding.rvBaseCatBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    //7 ::00 #20 Modern android ecommerce app | Finish Categories





}
