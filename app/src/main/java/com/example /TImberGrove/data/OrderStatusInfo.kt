package com.example.timbertrove.data.ordersData

import java.util.Objects

sealed class OrderStatusInfo(val statusInfo: String){
    object Ordered : OrderStatusInfo("Ordered")
    object Canceled : OrderStatusInfo("Canceled")
    object Confirmed : OrderStatusInfo("Confirmed")
    object Shipped : OrderStatusInfo("Shipped")
    object Delivered : OrderStatusInfo("Delivered")
    object Returned : OrderStatusInfo("Returned")
}

fun getOrderStatus(status: String): OrderStatusInfo {
    return when (status) {
        "Ordered" -> {
            OrderStatusInfo.Ordered
        }

        "Canceled" -> {
            OrderStatusInfo.Canceled
        }

        "Confirmed" -> {
            OrderStatusInfo.Confirmed
        }

        "Shipped" -> {
            OrderStatusInfo.Shipped
        }

        "Delivered" -> {
            OrderStatusInfo.Delivered
        }

        else -> OrderStatusInfo.Returned
    }
}

