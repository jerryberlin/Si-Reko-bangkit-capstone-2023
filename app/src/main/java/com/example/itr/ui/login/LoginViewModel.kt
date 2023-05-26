package com.example.itr.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itr.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _validation = Channel<LoginFieldState>()
    val validation = _validation.receiveAsFlow()

    fun login(email: String, password: String) {
        if (checkValidation(email, password)) {
            viewModelScope.launch {
                _login.emit(Resource.Loading())
            }
            firebaseAuth.signInWithEmailAndPassword(
                email, password
            ).addOnSuccessListener {
                viewModelScope.launch {
                    it.user?.let {
                        _login.emit(Resource.Success(it))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }
            }
        } else {
            val loginFieldState = LoginFieldState(
                validateEmail(email),
                validatePassword(password)
            )

            runBlocking {
                _validation.send(loginFieldState)
            }
        }
    }

    private fun checkValidation(email: String, password: String): Boolean {
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)
        return emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
    }
}
