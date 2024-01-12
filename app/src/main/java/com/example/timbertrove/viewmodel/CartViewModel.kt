package com.example.timbertrove.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timbertrove.data.CartProductInfo
import com.example.timbertrove.helper.getProductPrice
import com.example.timbertrove.utils.FirebaseCommon
import com.example.timbertrove.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore : FirebaseFirestore,
    private val auth : FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel(){

    private val _cartroducts = MutableStateFlow<Resource<List<CartProductInfo>>>(Resource.Unspecified())
    val cartProducts = _cartroducts.asStateFlow()
    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    // delete dialog for items at 0
    private val _deleteDialog = MutableSharedFlow<CartProductInfo>()
    val deleteDialog = _deleteDialog.asSharedFlow()
    fun deleteCartProducts(cartProduct: CartProductInfo){
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1){
            val documentId = cartProductDocuments[index].id
            firestore
                .collection("users")
                .document(auth.uid!!)
                .collection("cart")
                .document(documentId)
                .delete()
        }
    }


    // calculating the total price
    val productsPrice = cartProducts.map { cartProductInfoList ->
        when(cartProductInfoList){
            is Resource.Success -> {calculateProductPrice(cartProductInfoList.data!!)}
            else -> null
        }
    }
    // helper function to calculate the total price
    private fun calculateProductPrice(data: List<CartProductInfo>): Float {
        return  data.sumByDouble { cartProductInfo ->
            (cartProductInfo.productInfo.offerPercentage.getProductPrice(cartProductInfo.productInfo.price) * cartProductInfo.quantity).toDouble()
        }.toFloat()
    }



    init {
        getCartProducts()
    }


    private fun getCartProducts(){
        viewModelScope.launch { _cartroducts.emit(Resource.Loading()) }

        firestore.collection("users")
            .document(auth.uid!!)
            .collection("cart")
            .addSnapshotListener{ value, error ->  // add snapshot listeners is used to see and get every time there is a change.  It will refresh the data.
                if(error != null || value== null){
                    viewModelScope.launch { _cartroducts.emit(Resource.Error(error?.message.toString())) }
                }
                else
                {
                    cartProductDocuments = value.documents
                    val cartProducts = value.toObjects(CartProductInfo::class.java)
                    viewModelScope.launch { _cartroducts.emit(Resource.Success(cartProducts)) }
                }
            }
    }

    fun changeQuantity( cartProduct: CartProductInfo, quantityChanging: FirebaseCommon.QuantityChanging){

        val index = cartProducts.value.data?.indexOf(cartProduct)
        /*
        * index could be equal to -1 if the function[get cart product] delays which will also delay the result we expect to be inside the [_cartProducts]
        * and to prevent the app from crashing we make a check.
        * */
        if(index != null && index != -1){
            val documentId = cartProductDocuments[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE ->{
                    viewModelScope.launch { _cartroducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1){
                        viewModelScope.launch {  _deleteDialog.emit(cartProduct)}
                        return
                    }
                    viewModelScope.launch { _cartroducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
                else -> Unit
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { result, exception ->
            if(exception != null){
                viewModelScope.launch { _cartroducts.emit(Resource.Error(exception.message.toString())) }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){ result, exception ->
            if(exception != null){
                viewModelScope.launch { _cartroducts.emit(Resource.Error(exception.message.toString())) }
            }

        }
    }


}