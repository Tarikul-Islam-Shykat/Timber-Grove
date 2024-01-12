package com.example.timbertrove.utils

import com.example.timbertrove.data.CartProductInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FirebaseCommon(private val firestore : FirebaseFirestore, private val auth : FirebaseAuth) {

    private val cartCollection = firestore
        .collection("users")
        .document(auth.uid!!)
        .collection("cart")

    fun addProductToCart(cartProductInfo: CartProductInfo, onResult: (CartProductInfo?, Exception?)-> Unit){
        cartCollection
            .document()
            .set(cartProductInfo)
            .addOnSuccessListener {
                onResult(cartProductInfo, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun increaseQuantity(documentId : String, onResult: (String?, Exception?) -> Unit){

        firestore.runTransaction { transition ->
            // with firebase transaction you make sure that all the operations happens  at once.
            // if any of the operations failed. the whole transaction will fail.
            // run transaction is used for read and write at the same time

            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProductInfo::class.java)
            productObject?.let {  cartProductInfo ->
                val newQuantity = cartProductInfo.quantity + 1
                val newProductObject = cartProductInfo.copy(quantity =  newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }

    }

    fun decreaseQuantity(documentId : String, onResult: (String?, Exception?) -> Unit){ // immplented for decreasign product in the cartDetails fragment.

        firestore.runTransaction { transition ->
            // with firebase transaction you make sure that all the operations happens  at once.
            // if any of the operations failed. the whole transaction will fail.
            // run transaction is used for read and write at the same time

            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProductInfo::class.java)
            productObject?.let {  cartProductInfo ->
                val newQuantity = cartProductInfo.quantity - 1
                val newProductObject = cartProductInfo.copy(quantity =  newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }

    }

    enum class QuantityChanging{
        INCREASE,DECREASE
    }
}


// copy -> helps to change only one data or one argument of the object.
