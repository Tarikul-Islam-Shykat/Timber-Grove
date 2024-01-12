package com.example.timbertrove.ui.billing

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
import com.example.timbertrove.databinding.FragmentUserProfileBinding
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest


class UserProfileFragment : Fragment() {

    private lateinit var binding : FragmentUserProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()
    private var imageUri : Uri?= null
    private lateinit var imageActivityResultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            imageUri =  it.data?.data
            Glide.with(this).load(imageUri).into(binding.imageUser)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // collect userInfo
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {  userInfo ->
                when(userInfo){
                    is Resource.Loading ->{
                        showUserLoading()
                    }
                    is Resource.Success ->{
                        hideUserLoading()
                        showUserInfo(userInfo.data!!)
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(), userInfo.message, Toast.LENGTH_SHORT)
                    }
                    else -> Unit
                }
            }
        }

        // update user info
        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {userInfo ->
                when(userInfo){
                    is Resource.Loading ->{
                        binding.buttonSave.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(), userInfo.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }

            }
        }

        // button save info clicked
        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName =edFirstName.text.toString().trim()
                val lastName = edLastName.text.toString().trim()
                val email = edEmail.text.toString().trim()
                val user = UserInfo(firstName, lastName, email)
                viewModel.updateUserInfo(user, imageUri)
            }
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "Image/*"
            imageActivityResultLauncher.launch(intent)
        }

    }

    private fun showUserInfo(data : UserInfo) {
        binding.apply {
            Glide
                .with(this@UserProfileFragment)
                .load(data.imagePath)
                .error(ColorDrawable(Color.BLACK))
                .into(imageUser)
            edFirstName.setText(data.firstName)
            edLastName.setText(data.lastName)
            edEmail.setText(data.email)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.GONE
            imageEdit.visibility = View.GONE
            edFirstName.visibility = View.GONE
            edLastName.visibility = View.GONE
            edEmail.visibility = View.GONE
            tvUpdatePassword.visibility = View.GONE
            buttonSave.visibility  = View.GONE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility  = View.VISIBLE
        }
    }



}
