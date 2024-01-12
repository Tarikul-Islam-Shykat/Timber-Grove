package com.example.timbertrove.ui.dashboardFragment

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.timbertrove.R
import com.example.timbertrove.data.UserInfo
import com.example.timbertrove.databinding.FragmentProfileBinding
import com.example.timbertrove.ui.activity.LoginRegisterActivity
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.utils.showBottomNavigation
import com.example.timbertrove.viewmodel.ProfileViewModel
import com.example.timbertrove.viewmodel.SettingsViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    val viewModel by viewModels<SettingsViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userProfileFragment)
        }

        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_orderHistoryFragment)

        }

        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f, emptyArray())
            findNavController().navigate(action)
        }

        binding.linearLogOut.setOnClickListener {
            viewModel.logOut()
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()  // this will make sure you removed the activity
        }

        // getting user info
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest { userInfo ->
                when(userInfo){

                    is Resource.Loading ->{
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }

                    is Resource.Success ->{
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView())
                            .load(userInfo.data!!.imagePath)
                            .error(ColorDrawable(Color.BLACK))
                            .into(binding.imageUser)
                    }

                    is Resource.Error ->{
                        Toast.makeText(requireContext(), userInfo.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }



}