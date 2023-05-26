package com.example.itr.util

sealed class RegisterValidation() {
    object Success : RegisterValidation()
    data class Failed(val message: String) : RegisterValidation()
}

data class RegisterFieldState(
    val name: RegisterValidation,
    val email: RegisterValidation,
    val password: RegisterValidation
)

data class LoginFieldState(
    val email: RegisterValidation,
    val password: RegisterValidation
)