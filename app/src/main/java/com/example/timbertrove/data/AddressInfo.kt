package com.example.timbertrove.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressInfo(
    val addressTitle : String,
    val fullName : String,
    val street : String,
    val phone : String,
    val city : String,
    val state : String
) : Parcelable{
    constructor() : this("", "", "", "", "", "")
}
