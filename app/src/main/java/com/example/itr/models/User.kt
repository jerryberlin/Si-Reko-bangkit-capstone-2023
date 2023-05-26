package com.example.itr.models

data class User(
    val name: String,
    val email: String,
    val imagePath: String = "",
) {
    constructor() : this("", "", "")
}
