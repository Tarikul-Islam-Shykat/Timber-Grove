package com.example.timbertrove.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timbertrove.data.UserInfo
import com.example.timbertrove.utils.Resource
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewHolder @Inject constructor(
    private val fireStore : FirebaseFirestore,
    private val auth : FirebaseAuth
) : ViewModel(){

    private val _user = MutableStateFlow<Resource<UserInfo>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    fun getUser(){

        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        fireStore
            .collection("users")
            .document(auth.uid!!)
            .addSnapshotListener{ value, error ->
                if(error != null){
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                    }
                }
                else{
                    val user = value?.toObject(UserInfo::class.java)
                    user?.let {
                        viewModelScope.launch {
                            _user.emit(Resource.Success(user))
                        }
                    }
                }
            }


    }

    fun logOut() {
        auth.signOut()
    }

}
