package com.example.timbertrove.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.timbertrove.R
import com.example.timbertrove.databinding.FragmentLoginBinding
import com.example.timbertrove.ui.Dialog.setUpBottomSheetDialog
import com.example.timbertrove.ui.activity.ShopingActivity
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private  lateinit var  binding : FragmentLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLoginPageLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString().trim()
                loginViewModel.login(email =  email, password =  password)
            }

            binding.txtDontHaveAcount.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            binding.txtForgetPassword.setOnClickListener {
                setUpBottomSheetDialog { email ->
                    loginViewModel.resetPassword(email)
                }
            }

        }

        lifecycleScope.launchWhenStarted {
            loginViewModel.resetPassword.collect{ resources ->
                when(resources){

                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        Snackbar.make(requireView(), "Reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error sending email. ", Snackbar.LENGTH_LONG).show()
                    }

                    else -> Unit

                }
            }
        }


        lifecycleScope.launchWhenStarted {
            loginViewModel.login.collect{resources ->
                when(resources){

                    is Resource.Loading -> {
                        binding.btnLoginPageLogin.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnLoginPageLogin.revertAnimation()
                        Intent(requireActivity(), ShopingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // makes sure not going to back to login activity
                            startActivity(intent)
                        }
                    }

                    is Resource.Error -> {
                        binding.btnLoginPageLogin.revertAnimation()
                        Toast.makeText(requireContext(), resources.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit


                }
            }
        }
    }

}