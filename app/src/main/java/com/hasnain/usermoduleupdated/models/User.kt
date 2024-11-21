package com.hasnain.usermoduleupdated.models

data class User(
    val user_name: String = "",
    val user_email: String = "",
    val user_cnic: String = "",
    val user_password: String = "",
    val user_cnic_img_url: String = "",
    var user_account_status: String = "" ,// null = underVerification, true = verified, false = rejected
    var user_phone: String = "" // null = underVerification, true = verified, false = rejected
)
