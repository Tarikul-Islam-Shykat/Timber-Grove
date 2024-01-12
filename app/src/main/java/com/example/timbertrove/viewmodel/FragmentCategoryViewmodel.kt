package com.example.timbertrove.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timbertrove.data.CategoryInfo
import com.example.timbertrove.data.ProductInfo
import com.example.timbertrove.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FragmentCategoryViewmodel constructor(
    private val firestore: FirebaseFirestore,
    private val categoryInfo: CategoryInfo
) : ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<ProductInfo>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<ProductInfo>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    init {
        fetchOfferproducts()
        fetchBestrproducts()
    }

    fun fetchOfferproducts(){
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }

        firestore
            .collection("Products")
            .whereEqualTo("category", categoryInfo.categoryInfo)
            .whereNotEqualTo("offerPercentage", null)
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.toObjects(ProductInfo::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestrproducts(){
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }

        firestore
            .collection("Products")
            .whereEqualTo("category", categoryInfo.categoryInfo)
            .whereEqualTo("offerPercentage", null)
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.toObjects(ProductInfo::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }



}