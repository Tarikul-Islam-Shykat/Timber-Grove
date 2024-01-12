package com.example.timbertrove.helper

fun Float?.getProductPrice(price : Float) : Float{
    // this -> percentage

    if (this == null)
        return price

    val remainPricePercentage = 1f - this
    val piceAfterOffer = remainPricePercentage * price
    return piceAfterOffer
}