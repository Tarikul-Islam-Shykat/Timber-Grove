package com.example.timbertrove.data

sealed class CategoryInfo(val categoryInfo: String) {
    object Chair : CategoryInfo("Chairs")
    object Cupboard : CategoryInfo("Cupboard")
    object Table : CategoryInfo("Table")
    object Accessory : CategoryInfo("Accessory")
    object Furniture : CategoryInfo("Furniture") // done
}