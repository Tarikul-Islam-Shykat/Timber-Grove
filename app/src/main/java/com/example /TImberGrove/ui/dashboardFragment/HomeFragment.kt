package com.example.timbertrove.ui.dashboardFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timbertrove.R
import com.example.timbertrove.adapters.HomeViewPagerAdapter
import com.example.timbertrove.databinding.FragmentHomeBinding
import com.example.timbertrove.ui.categoriesFragments.AccessoryFragment
import com.example.timbertrove.ui.categoriesFragments.ChairFragment
import com.example.timbertrove.ui.categoriesFragments.CupBoardFragment
import com.example.timbertrove.ui.categoriesFragments.FurnitureFragment
import com.example.timbertrove.ui.categoriesFragments.MainCategoryFragment
import com.example.timbertrove.ui.categoriesFragments.TableFragment
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragment = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupBoardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        binding.viewPageHome.isUserInputEnabled = false // this is for not sliding left while sliding the products.

        val viewpage2Adapter = HomeViewPagerAdapter(categoriesFragment, childFragmentManager, lifecyele = lifecycle)
        binding.viewPageHome.adapter = viewpage2Adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPageHome){ tab, position ->
            when(position){
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
            }
        }.attach()


    }


}
