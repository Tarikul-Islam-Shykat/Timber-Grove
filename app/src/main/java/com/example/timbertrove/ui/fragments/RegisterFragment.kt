package com.example.timbertrove.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.timbertrove.R
import com.example.timbertrove.data.UserInfo
import com.example.timbertrove.databinding.FragmentRegisterBinding
import com.example.timbertrove.utils.RegisterValidation
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext


private val tag_ = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var  binding: FragmentRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtAlreadHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // saving the values
        binding.apply {
            btnRegisterPageRegister.setOnClickListener {
                val user_info =  UserInfo(
                   firstName =  edFirstNameReg.text.toString().trim(),
                    lastName =  edLastNameReg.text.toString().trim(),
                    email = edEmailReg.text.toString().trim(),
                    ""
                )
                val password_ = edPasswordReg.text.toString()
                registerViewModel.createAccountWithEmailAndPassword(user_info, password_)
            }
        }

        // launching life cycle
        lifecycleScope.launchWhenStarted {
            registerViewModel.register.collect{ userData ->
                when(userData){
                    is Resource.Loading -> {
                        Log.d(tag_, "loading" )
                        binding.btnRegisterPageRegister.startAnimation()
                    }

                    is Resource.Success -> {
                        Log.d(tag_, "Success" )
                        Log.d(tag_, userData.data.toString() )
                        binding.btnRegisterPageRegister.revertAnimation()
                    }

                    is Resource.Error -> {
                        Log.d(tag_, "falied" )
                        Log.d(tag_, userData.message.toString())
                        binding.btnRegisterPageRegister.revertAnimation()
                    }

                    else -> Unit

                }
            }
        }  // end lifecycle scope code

        // validation check
        lifecycleScope.launchWhenStarted {
            registerViewModel.validation.collect{ registerFieldState ->

                if(registerFieldState.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edEmailReg.apply {
                            requestFocus()
                            error = registerFieldState.email.message
                        }
                    }
                }

                if(registerFieldState.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.edPasswordReg.apply {
                            requestFocus()
                            error = registerFieldState.password.message
                        }
                    }
                }


            }
        }



    }




}