package com.example.timbertrove.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.timbertrove.data.CategoryInfo
import com.example.timbertrove.viewmodel.FragmentCategoryViewmodel
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryViewModelFactory
    ( private val firestore: FirebaseFirestore, private val categoryInfo: CategoryInfo)
    : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FragmentCategoryViewmodel(firestore, categoryInfo) as T
    }

}
