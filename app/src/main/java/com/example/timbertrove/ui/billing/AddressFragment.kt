package com.example.timbertrove.ui.billing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.timbertrove.data.AddressInfo
import com.example.timbertrove.databinding.FragmentAddressBinding
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.viewmodel.FragmentAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private lateinit var binding : FragmentAddressBinding
    val viewModel by viewModels<FragmentAddressViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // collecting info
        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddressInFireStore.collectLatest {  addressInfoState ->
                when(addressInfoState){
                    is Resource.Loading ->{
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.GONE
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), addressInfoState.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        // if there any error occured
        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val state = edState.text.toString()

                val address = AddressInfo(addressTitle, fullName, street,phone, city, state)
                viewModel.addAddressInFireStore(address)
            }
        }
    }


}