package com.example.timbertrove.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.timbertrove.R
import com.example.timbertrove.databinding.FragmentAccountOptionBinding
import com.example.timbertrove.databinding.FragmentIntroductionBinding


class AccountOptionFragment : Fragment() {

    private lateinit var binding : FragmentAccountOptionBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountOptionBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnOptionLogin.setOnClickListener{
            findNavController().navigate(R.id.action_accountOptionFragment_to_loginFragment)
        }

        binding.btnOptionRegister.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionFragment_to_registerFragment)

        }
    }

}