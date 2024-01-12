package com.example.timbertrove.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProductInfo(
    val productInfo: ProductInfo,
    val quantity : Int,
    val selectedColor : Int? = null,
    val selectedSize : String? = null
) : Parcelable{
    constructor() : this(ProductInfo(), 1, null, null) // for firebase.. an empty constructor is needed.
}