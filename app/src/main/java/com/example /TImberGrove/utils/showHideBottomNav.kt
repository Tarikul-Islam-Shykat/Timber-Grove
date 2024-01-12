package com.example.timbertrove.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.example.timbertrove.R
import com.example.timbertrove.ui.activity.ShopingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


fun Fragment.hideBottomNavigation(){
    // hiding the bottom navigation view.
    val bottomNavigationView = (activity as ShopingActivity).findViewById<BottomNavigationView>(R.id.bottomNav)
    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNavigation(){
    // hiding the bottom navigation view.
    val bottomNavigationView = (activity as ShopingActivity).findViewById<BottomNavigationView>(R.id.bottomNav)
    bottomNavigationView.visibility = View.VISIBLE
}

