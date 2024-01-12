package com.example.timbertrove.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.timbertrove.R
import com.example.timbertrove.databinding.FragmentIntroductionBinding
import com.example.timbertrove.ui.activity.ShopingActivity
import com.example.timbertrove.viewmodel.InroductionViewModel
import com.example.timbertrove.viewmodel.InroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.timbertrove.viewmodel.InroductionViewModel.Companion.SHOPPPING_ACTIVITY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class IntroductionFragment : Fragment() {

    private lateinit var  binding : FragmentIntroductionBinding
    private val viewModel by viewModels<InroductionViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIntroductionBinding.inflate(layoutInflater)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect{
                when(it){
                    SHOPPPING_ACTIVITY ->{ // import com.example.timbertrove.viewmodel.InroductionViewModel.Companion.SHOPPPING_ACTIVITY
                        Intent(requireActivity(), ShopingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    ACCOUNT_OPTIONS_FRAGMENT ->{  //import com.example.timbertrove.viewmodel.InroductionViewModel.Companion.SHOPPPING_ACTIVITY
                        findNavController().navigate(it)
                    }
                    else -> Unit
                }
            }
        }


        /*var user = Firebase.auth.currentUser
        if(user != null){
            var intent = Intent(activity, ShopingActivity::class.java)
            startActivity(intent)
        }*/



        binding.btnStart.setOnClickListener{
            viewModel.startButtonClicked()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionFragment)
        }
    }


}
