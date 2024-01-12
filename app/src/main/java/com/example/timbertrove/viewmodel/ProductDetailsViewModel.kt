package com.example.timbertrove.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timbertrove.data.CartProductInfo
import com.example.timbertrove.utils.FirebaseCommon
import com.example.timbertrove.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductDetailsViewModel // its for product details. mistakenly as cart Details. But in the documentation, it will be as CartDetails fragment.
    @Inject constructor(
        private val fireStore : FirebaseFirestore,
        private val auth : FirebaseAuth,
        private val firebaseCommon: FirebaseCommon
    ) : ViewModel()
{

    private val _addToCart = MutableStateFlow<Resource<CartProductInfo>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cartProductInfo: CartProductInfo){
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }

        fireStore
            .collection("users")
            .document(auth.uid!!)
            .collection("cart")
            .whereNotEqualTo("product.id", cartProductInfo.productInfo.id)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.let { listDocument ->
                    if(listDocument.isEmpty()){ // add new product
                        addNewProduct(cartProductInfo)
                    }
                    else{ // increase the quantity
                        val product = listDocument.first().toObject(CartProductInfo::class.java)
                            if(product == cartProductInfo) { // increase the quantity
                                val documentId = listDocument.first().id
                                increaseQuantity(documentId, cartProductInfo)
                            }
                            else{ // add new product
                                addNewProduct(cartProductInfo)
                            }
                    }
                }
            }
            .addOnFailureListener {

            }
    }

    private fun addNewProduct(cartProductInfo: CartProductInfo){
        firebaseCommon.addProductToCart(cartProductInfo) { addedProduct, e ->
            viewModelScope.launch {
                if(e == null){
                    _addToCart.emit(Resource.Success(addedProduct!!))
                }
                else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    fun increaseQuantity(documentId : String, cartProduct : CartProductInfo){
        firebaseCommon.increaseQuantity(documentId){ _, e ->
            viewModelScope.launch {
                if(e == null){
                    _addToCart.emit(Resource.Success(cartProduct))
                }
                else{
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }






}