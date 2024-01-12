package com.example.timbertrove.utils

import android.util.Patterns

fun ValidationEmail(email : String) : RegisterValidation {

    if(email.isEmpty())
        return  RegisterValidation.Failed("Email must not be empty")

    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong Email Format")

    return RegisterValidation.Success

}

fun ValidationPassword(password : String) : RegisterValidation {

    if(password.isEmpty())
        return  RegisterValidation.Failed("Password must not be empty")

    if(password.length < 6)
        return RegisterValidation.Failed("Password must be at least 6 characters")

    return RegisterValidation.Success

}
