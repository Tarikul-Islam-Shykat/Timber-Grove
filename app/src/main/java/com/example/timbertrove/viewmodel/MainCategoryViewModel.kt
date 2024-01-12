package com.example.timbertrove.viewmodel

import android.graphics.pdf.PdfDocument.PageInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timbertrove.data.ProductInfo
import com.example.timbertrove.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(private val firestore: FirebaseFirestore) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<ProductInfo>>>(Resource.Unspecified())
    val speacialProducts: StateFlow<Resource<List<ProductInfo>>> = _specialProducts

    private val _bestDealsProduct = MutableStateFlow<Resource<List<ProductInfo>>>(Resource.Unspecified())
    val _bestdealProducts : StateFlow<Resource<List<ProductInfo>>> = _bestDealsProduct

    private val _bestProduct = MutableStateFlow<Resource<List<ProductInfo>>>(Resource.Unspecified())
    val _bestProducts : StateFlow<Resource<List<ProductInfo>>> = _bestProduct

    private val pagingInfo = paginInfo()



    init {
        fetchSpecialProducts()
        fetchBestDeaslProducts()
        fetchBestProducts()
    }

    fun fetchSpecialProducts(){
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category","Special Products")
            .get()
            .addOnSuccessListener { result ->
                // converting result to product object
                val specialProductList = result.toObjects(ProductInfo::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductList))
                }
            }
            .addOnFailureListener { result ->
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(result.message.toString()))
                }
            }
    }

    fun fetchBestDeaslProducts(){
        viewModelScope.launch {
            _bestDealsProduct.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category","Best Deals")
            .get()
            .addOnSuccessListener { result ->
                // converting result to product object
                val bestProductList = result.toObjects(ProductInfo::class.java)
                viewModelScope.launch {
                    _bestDealsProduct.emit(Resource.Success(bestProductList))
                }
            }
            .addOnFailureListener { result ->
                viewModelScope.launch {
                    _bestDealsProduct.emit(Resource.Error(result.message.toString()))
                }
            }
    }

    fun fetchBestProducts(){
        if(!pagingInfo.isPaginEnd){
            viewModelScope.launch {
                _bestProduct.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .whereEqualTo("category","Best Products")
                .limit(pagingInfo.bestProductPage * 10)
                .get()
                .addOnSuccessListener { result ->
                    // converting result to product object
                    val bestProductList = result.toObjects(ProductInfo::class.java)

                    // checking if the fetched oldBestProduct = fetched best products. (to stop the paging )
                    // if its not , then we can continue to paging it.
                    // it will help to stop the progress bar and also to stop request for more products from firebase.
                    pagingInfo.isPaginEnd =  bestProductList == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = bestProductList

                    viewModelScope.launch {
                        _bestProduct.emit(Resource.Success(bestProductList))
                    }
                    pagingInfo.bestProductPage++ // this will extend the page number to get more 10 products. so that not every products comes. initally start as 1
                }
                .addOnFailureListener { result ->
                    viewModelScope.launch {
                        _bestProduct.emit(Resource.Error(result.message.toString()))
                    }
                }
        }

    }


    internal data class paginInfo ( // to avoid calling all the data at once.
         var bestProductPage : Long = 1,
         // want to make sure at the end no progressbar shows up
        var oldBestProducts : List<ProductInfo> = emptyList(),
        var isPaginEnd : Boolean = false
    )

}