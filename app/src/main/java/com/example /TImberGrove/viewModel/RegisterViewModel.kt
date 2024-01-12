package com.example.timbertrove.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timbertrove.data.UserInfo
import com.example.timbertrove.utils.Constants
import com.example.timbertrove.utils.RegisterFieldsState
import com.example.timbertrove.utils.RegisterValidation
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.utils.ValidationEmail
import com.example.timbertrove.utils.ValidationPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
    @Inject constructor(
        private var firebaseAuth: FirebaseAuth,
        private val db : FirebaseFirestore
    )
    : ViewModel()
{

    private  val _register = MutableStateFlow<Resource<UserInfo>>(Resource.Unspecified())
    val register: Flow<Resource<UserInfo>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()


    fun createAccountWithEmailAndPassword(user : UserInfo, password : String){

        if( checkValidation(user, password) ) {
            runBlocking {
                _register.emit(Resource.Loading())
            }

            // creating a new account
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let { firebaseUser ->
                        saveUserInfo(firebaseUser.uid,user)
                    }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        }
        else{
           val registerFieldsState = RegisterFieldsState(
               ValidationEmail(user.email), ValidationPassword(password)
           )
            viewModelScope.launch {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(userUid : String, user: UserInfo) {
        db.collection(Constants.USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user) // saving in the auth
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())

            }
    }

    private fun checkValidation(user: UserInfo, password: String) : Boolean {

        val emailValidation = ValidationEmail(user.email) // from ValidationCheck.kt
        val passwordValidation = ValidationPassword(password)
        val shouldRegister =
            emailValidation is RegisterValidation.Success &&
                    passwordValidation is RegisterValidation.Success
        return shouldRegister

    }
}

