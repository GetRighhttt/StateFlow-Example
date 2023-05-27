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

    companion object {
        const val TAG = "VIEW_MODEL"
    }

    init {
        Log.d(TAG, "MainViewModel Initialized.")

        // sets an idle state for the login variable to wait for the text
        _loginState.value = LoginState.Idle
        Log.d(TAG, "Login state is IDLE...")
    }

    fun login(username: String, password: String) {

        /*
        Using our Login data class for our username and password values.
        Kotlin destructing syntax allows us to do this without creating an object
        of that class.
         */
        val (name, pass) = Login(username, password)

        try {
            viewModelScope.launch {
                _loginState.value = LoginState.Loading
                delay(1000L)
                if (name == "username" && pass == "password") {
                    _loginState.value =
                        LoginState.Success(successMessage = "Success! You have successfully logged in.")
                } else {
                    _loginState.value =
                        LoginState.Failure(errorMessage = "Login attempt failed. Please try again.")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error when launching viewModelScope: $e")
        }
    }

    // sets empty state in cleared method in main.
    fun setEmptyState() {
        _loginState.value = LoginState.Empty
        Log.d(TAG, "Login state is EMPTY...")
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