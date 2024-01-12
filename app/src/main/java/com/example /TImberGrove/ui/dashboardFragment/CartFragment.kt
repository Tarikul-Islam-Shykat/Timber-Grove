package com.example.timbertrove.ui.dashboardFragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timbertrove.R
import com.example.timbertrove.adapters.CartProductAdapter
import com.example.timbertrove.databinding.FragmentCartBinding
import com.example.timbertrove.helper.VerticalItemDecoration
import com.example.timbertrove.utils.FirebaseCommon
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var  binding : FragmentCartBinding
    private  val cartAdapter  by lazy { CartProductAdapter() }

    // as we already using the view model in the shoping fragment for showing the number of items in the cart.
    // so for this reason we want to the same view model. not a new obejct of the view model
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setting up the recycler view
        setUpCartRV()
        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest { cartProductInfoList ->
                when(cartProductInfoList){
                    is Resource.Loading ->{
                        binding.progressbarCart.visibility = View.VISIBLE
                    }

                    is Resource.Success ->{
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if(cartProductInfoList.data!!.isEmpty()){
                            showEmptyCart()
                            hideOtherViews()
                        }
                        else{
                            showOtherViews()
                            hideEmptyCart()
                            cartAdapter.differ.submitList(cartProductInfoList.data)
                        }
                    }
                    is Resource.Error ->{
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), cartProductInfoList.toString(), Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit

                }
            }
        }

        // getting the total price
        var total_price = 0f // for sending to billing
        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    total_price = it
                    binding.tvTotalPrice.text = "$ $price"
                }
            }
        }

        // on product click navigate to product info
        cartAdapter.onProductClick = { cartProductInfo ->
            val bundle = Bundle().apply {  putParcelable("product", cartProductInfo.productInfo)}
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, bundle)
        }

        // on plus click
        cartAdapter.onPlusClick = { cartProduct ->
            viewModel.changeQuantity(cartProduct, FirebaseCommon.QuantityChanging.INCREASE)
        }

        // on Minus click
        cartAdapter.onMinusClick = { cartProduct ->
            viewModel.changeQuantity(cartProduct, FirebaseCommon.QuantityChanging.DECREASE)
        }

        // when product items goes to 0 delete dialog
        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart")
                    setNegativeButton("Cancel"){ dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes"){ dialog, _ ->
                        viewModel.deleteCartProducts(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        // add to cart click
        binding.btnCheckOut.setOnClickListener {
            val action  = CartFragmentDirections.actionCartFragmentToBillingFragment(total_price, cartAdapter.differ.currentList.toTypedArray())
            findNavController().navigate(action)
        }


    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            btnCheckOut.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            btnCheckOut.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility = View.VISIBLE
        }
    }

    private fun setUpCartRV() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration()) // for adding some space in between
        }
    }


}
