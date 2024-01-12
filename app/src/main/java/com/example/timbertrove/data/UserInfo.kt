package com.example.timbertrove.data

data class UserInfo(
    val firstName : String,
    val lastName : String,
    val email : String,
    val imagePath : String = ""
)
{
    constructor() : this("", "", "","") // this is neccessary for firebase.
}
