package com.example.stateflow

import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat.animate
import com.example.stateflow.databinding.ActivityMainBinding
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // declaring binding and view model variables
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {

            // login button
            btnLogin.setOnClickListener {
                setViewModelLoginMethod()
            }

            // clear button
            btnClear.setOnClickListener {
                clearScreen().also {
                    Snackbar.make(
                        binding.root,
                        "Cleared Screen, setting Empty State.",
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction("OK") { _ -> Unit }.show()
                }
            }
        }

        // sets the login state
        determineLoginState()
    }

    private fun setViewModelLoginMethod() {
        binding.apply {
            viewModel.login(
                etLogin.text.toString(),
                etPassword.text.toString()
            )
            Log.d("MAIN", "Login: $etLogin, $etPassword")
            // add rotation animation to FAB
            btnLogin.animate().rotationY(180F).duration = 500L
        }
    }

    private fun determineLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect {
                when (it) {
                    is MainViewModel.LoginState.Success -> {
                        Snackbar.make(binding.root, it.successMessage, Snackbar.LENGTH_SHORT).show()
                        binding.apply {
                            progressBar.visibility = View.GONE
                        }
                        Log.d("MAIN", "Login successful.")
                    }

                    is MainViewModel.LoginState.Failure -> {
                        Snackbar.make(binding.root, it.errorMessage, Snackbar.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        Log.d("MAIN", "Login failed.")
                    }

                    is MainViewModel.LoginState.Loading -> {
                        Snackbar.make(binding.root, "Loading State", Snackbar.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d("MAIN", "Loading...")
                    }

                    is MainViewModel.LoginState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d("MAIN", "Currently idle...")
                    }

                    is MainViewModel.LoginState.Empty -> {
                        Log.d("MAIN", "Currently empty...")
                    }
                }
            }
        }
    }

    private fun clearScreen() {
        viewModel.setEmptyState()
        binding.apply {
            etLogin.text?.clear()
            etPassword.text?.clear()
            // add rotation animation to FAB
            btnClear.animate().rotationY(180F).duration = 500L
        }
        Log.d("MAIN", "Screen cleared!")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}