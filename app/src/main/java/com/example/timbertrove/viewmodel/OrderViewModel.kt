package com.example.timbertrove.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timbertrove.data.ordersData.OrderInfo
import com.example.timbertrove.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan


@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore : FirebaseFirestore,
    private  val auth:FirebaseAuth
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<OrderInfo>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(orderInfo: OrderInfo){

        viewModelScope.launch { _order.emit(Resource.Loading()) }

        // add the order into user orders collection
        firestore
            .collection("users")
            .document(auth.uid!!)
            .collection("orders")
            .document()
            .set(orderInfo)


        // add the order into orders collection for admin
        firestore
            .collection("orders")
            .document()
            .set(orderInfo)

        // delete the products from the user cart collection
        firestore.collection("users")
            .document(auth.uid!!)
            .collection("cart")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach{ documentSnapshot ->
                    documentSnapshot.reference.delete()
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _order.emit(Resource.Error(it.message.toString()))}
            }
    }

}