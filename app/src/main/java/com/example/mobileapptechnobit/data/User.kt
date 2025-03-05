package com.example.sisteminformasimenejemensatpam.data

class User(
    val id: Int, // AUTO_INCREMENT
    val username: String,
    val email: String,
    var password: String,
    val roleId: Int? = null, // Bisa NULL
    val name: String,
    val photo: String? = null, // Bisa NULL
) {

}