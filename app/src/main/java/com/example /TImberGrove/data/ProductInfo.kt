package com.example.timbertrove.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // 1
data class ProductInfo (
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>
) : Parcelable // 2
{
    constructor() : this("0", "name", "category", 0f,  images = emptyList()) // make sure to add this to avoid error.
    // its a neccesearry to add this for firebase.
    // java.lang.RuntimeException: Could not deserialize object. Class com.example.timbertrove.data.ProductInfo does not define a no-argument constructor. If you are using ProGuard, make sure these constructors are not stripped
}

/*
1.
implemented when the product will be shown in the product details.
So form one fragment to another fragment data transfer. parcellize is used.
2. same as 1. its just an extension of 'anotation'Parcelize
3. also add it in the nav graph. watch #23 part. 4:56minute. add it in the argument.
**/
