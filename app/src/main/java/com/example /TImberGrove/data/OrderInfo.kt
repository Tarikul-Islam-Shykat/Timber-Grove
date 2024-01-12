package com.example.timbertrove.data.ordersData

import android.os.Parcelable
import com.example.timbertrove.data.AddressInfo
import com.example.timbertrove.data.CartProductInfo
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

@Parcelize
data class OrderInfo(
    val orderStatus : String = "",
    val totalPrice : Float = 0f,
    val products : List<CartProductInfo> = emptyList(),
    val addreess : AddressInfo = AddressInfo(),

    val date : String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId : Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()
) : Parcelable
