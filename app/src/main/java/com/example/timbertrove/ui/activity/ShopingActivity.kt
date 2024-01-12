package com.example.timbertrove.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.timbertrove.R
import com.example.timbertrove.databinding.ActivityShopingBinding
import com.example.timbertrove.utils.Resource
import com.example.timbertrove.viewmodel.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ShopingActivity : AppCompatActivity() {


    val binding by lazy {
        ActivityShopingBinding.inflate(layoutInflater)
    }

    val viewmodel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNav.setupWithNavController(navController)

        lifecycleScope.launchWhenStarted {
            viewmodel.cartProducts.collectLatest {
                when(it) {
                    is Resource.Success -> {
                        val count = it.data?.size?:0
                        val bottoNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                        bottoNav.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_blue)
                        }
                    }
                    else -> Unit
                }
            }
        }


    }
}