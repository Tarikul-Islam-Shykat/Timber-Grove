package com.example.timbertrove.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timbertrove.data.AddressInfo
import com.example.timbertrove.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentAddressViewModel @Inject constructor (
    private val firestore: FirebaseFirestore,
    private val auth : FirebaseAuth
) : ViewModel(){

    private val _addNewAddressInFirestore = MutableStateFlow<Resource<AddressInfo>>(Resource.Unspecified())
    val addNewAddressInFireStore = _addNewAddressInFirestore.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddressInFireStore(addressInfo: AddressInfo){
        val validateInputs = validateInputs(addressInfo)

        if(validateInputs){
            firestore
                .collection("users")
                .document(auth.uid!!)
                .collection("address")
                .document()
                .set(addressInfo)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _addNewAddressInFirestore.emit(Resource.Success(addressInfo))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _addNewAddressInFirestore.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
        else{
            viewModelScope.launch {
                _error.emit("All filds are required")
            }
        }


    }

    private fun validateInputs(addressInfo: AddressInfo) : Boolean {
        return  addressInfo.addressTitle.trim().isNotEmpty() &&
                addressInfo.city.trim().isNotEmpty() &&
                addressInfo.phone.trim().isNotEmpty() &&
                addressInfo.state.trim().isNotEmpty() &&
                addressInfo.street.trim().isNotEmpty() &&
                addressInfo.fullName.trim().isNotEmpty()
    }


}