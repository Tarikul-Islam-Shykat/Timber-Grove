package com.example.timbertrove.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.timbertrove.data.ordersData.OrderInfo
import com.example.timbertrove.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewHolder @Inject constructor(
    private val fireStore : FirebaseFirestore,
    private val auth : FirebaseAuth
) : ViewModel() {

    private val _allOrders = MutableStateFlow<Resource<List<OrderInfo>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()

    init {
        getAllOrder()
    }

    fun getAllOrder(){
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading())
        }

        fireStore
            .collection("users")
            .document(auth.uid!!)
            .collection("orders")
            .get()
            .addOnSuccessListener {
                val orders = it.toObjects(OrderInfo::class.java)
                viewModelScope.launch {
                    _allOrders.emit(Resource.Success(orders))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _allOrders.emit(
                        Resource.Error(it.message.toString())
                    )
                }
            }

    }

}
