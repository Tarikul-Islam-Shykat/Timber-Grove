package com.example.timbertrove.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.timbertrove.data.UserInfo
import com.example.timbertrove.utils.RegisterValidation
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.utils.TimberTroveApplication
import com.example.timbertrove.utils.ValidationEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth : FirebaseAuth,
    private val storage : StorageReference,
    app : Application
) : AndroidViewModel(app){

    private val _user = MutableStateFlow<Resource<UserInfo>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<UserInfo>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUserInfo()
    }

     fun getUserInfo() {
         viewModelScope.launch {
             _user.emit(Resource.Loading())

             firestore
                 .collection("users")
                 .document(auth.uid!!)
                 .get()
                 .addOnSuccessListener { documentSnapshot ->
                     val userInfo = documentSnapshot.toObject(UserInfo::class.java)
                     userInfo?.let {  userInfo ->
                         viewModelScope.launch {
                             _user.emit(Resource.Success(userInfo))
                         }
                     }
                 }
                 .addOnFailureListener {
                     viewModelScope.launch {
                         _user.emit(
                             Resource.Error(it.message.toString())
                         )
                     }
                 }
         }
    }

    fun updateUserInfo(userInfo: UserInfo, imageUrl : Uri?){
        val areInputsValid = ValidationEmail(userInfo.email) is RegisterValidation.Success
                &&  userInfo.firstName.trim().isNotEmpty()
                && userInfo.lastName.trim().isNotEmpty()

        if(!areInputsValid){
            viewModelScope.launch {
                _user.emit(Resource.Error("Check Your Inputs"))
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }
        
        if (imageUrl == null){
            saveUserInfo(userInfo, true)
        }
        else
        {
            saveUserInfoWithNewImage(userInfo, imageUrl)
        }
        


    }

    private fun saveUserInfoWithNewImage(userInfo: UserInfo, imageUrl: Uri) {
        viewModelScope.launch {
            try {
                val imageBitMap = MediaStore.Images.Media.getBitmap(getApplication<TimberTroveApplication>().contentResolver, imageUrl)
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitMap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInfo(userInfo.copy(imagePath = imageUrl), false)
            }catch (e : Exception){
                viewModelScope.launch {
                    _user.emit(Resource.Error(e.message.toString()))
                }
            }
        }

    }

    private fun saveUserInfo(userInfo: UserInfo, shouldRetriveOldImage: Boolean) {
        firestore.runTransaction {  transaction ->
            val documentRef = firestore.collection("users").document(auth.uid!!)
            if(shouldRetriveOldImage)
            {
                val currentUser = transaction.get(documentRef).toObject(UserInfo::class.java)
                val newUser = userInfo.copy(imagePath = currentUser?.imagePath?:"")
                transaction.set(documentRef, newUser)
            }
            else{
                transaction.set(documentRef, userInfo)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Success(userInfo))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error(it.message.toString()))
            }
        }
    }


}
