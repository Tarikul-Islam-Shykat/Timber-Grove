package com.example.timbertrove.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timbertrove.R
import com.example.timbertrove.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InroductionViewModel  @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth : FirebaseAuth
): ViewModel(){

    private val _navigate = MutableStateFlow(0)
    val navigate : StateFlow<Int>  = _navigate

    companion object{
        const val SHOPPPING_ACTIVITY = 23
        var ACCOUNT_OPTIONS_FRAGMENT = R.id.action_introductionFragment_to_accountOptionFragment
    }

    init {
        val isButtonClicked = sharedPreferences.getBoolean(Constants.INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        if(user != null){
            viewModelScope.launch {
                _navigate.emit(SHOPPPING_ACTIVITY)
            }
        }
        else if (isButtonClicked){
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT)
            }
        }
        else{
            Unit
        }
    }

    fun startButtonClicked(){
        sharedPreferences.edit().putBoolean(Constants.INTRODUCTION_KEY, true).apply()
    }

}