package com.example.stateflow

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Empty)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        Log.d("VIEW_MODEL", "MainViewModel Initialized.")

        // sets an idle state for the login variable to wait for the text
        _loginState.value = LoginState.Idle
        Log.d("VIEW_MODEL", "Login state is IDLE...")
    }

    fun login(username: String, password: String) = viewModelScope.launch {

        _loginState.value = LoginState.Loading
        delay(3000L)
        if (username == "username" && password == "password") {
            _loginState.value =
                LoginState.Success(successMessage = "Success! You have successfully logged in.")
        } else {
            _loginState.value =
                LoginState.Failure(errorMessage = "Login attempt failed. Please try again.")
        }
    }

    // sets empty state in cleared method in main.
    fun setEmptyState() {
        _loginState.value = LoginState.Empty
        Log.d("VIEW_MODEL", "Login state is EMPTY...")
    }

    // Sealed class to handle state of Login.
    sealed class LoginState {
        object Empty : LoginState()
        object Idle : LoginState()
        data class Success(val successMessage: String) : LoginState()
        data class Failure(val errorMessage: String) : LoginState()
        object Loading : LoginState()
    }
}