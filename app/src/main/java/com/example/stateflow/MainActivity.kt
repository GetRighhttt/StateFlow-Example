package com.example.stateflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.example.stateflow.databinding.ActivityMainBinding
import androidx.lifecycle.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // declaring binding and view model variables
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    companion object {
        const val TAG = "MAIN"
    }

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
                materialDialog("Are you sure you want to clear all the data?")
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
            Log.d(TAG, "Login: $etLogin, $etPassword")
        }
    }

    private fun determineLoginState() {
        try {
            lifecycleScope.launch {
                viewModel.loginState.flowWithLifecycle(lifecycle).collect {
                    when (it) {
                        is MainViewModel.LoginState.Success -> {
                            materialDialog(it.successMessage)
                            binding.apply {
                                progressBar.visibility = View.GONE
                            }
                            Log.d(TAG, "Login successful.")
                        }

                        is MainViewModel.LoginState.Failure -> {
                            materialDialog(it.errorMessage)
                            binding.progressBar.visibility = View.GONE
                            Log.d(TAG, "Login failed.")
                        }

                        is MainViewModel.LoginState.Loading -> {
                            Snackbar.make(binding.root, "Loading State", Snackbar.LENGTH_SHORT)
                                .show()
                            binding.progressBar.visibility = View.VISIBLE
                            Log.d(TAG, "Loading...")
                        }

                        is MainViewModel.LoginState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                            Log.d(TAG, "Currently idle...")
                        }

                        is MainViewModel.LoginState.Empty -> {
                            Log.d(TAG, "Currently empty...")
                        }
                    }
                }
            }
        } catch (e: IllegalStateException) {
            Log.d(TAG, "IllegalStateException: ${e.message}")
        }
    }

    private fun clearScreen() {
        viewModel.setEmptyState()
        binding.apply {
            etLogin.text?.clear()
            etPassword.text?.clear()
            animateButton(btnClear)
        }
        Log.d(TAG, "Screen cleared!")
    }

    private fun materialDialog(stateMessage: String): MaterialAlertDialogBuilder =
        object : MaterialAlertDialogBuilder(this) {
            val dialog = MaterialAlertDialogBuilder(this@MainActivity)
                .setTitle("Login Dialog")
                .setMessage(stateMessage)
                .setPositiveButton("OK") { dialog, _ ->
                    clearScreen()
                    animateButton(binding.btnLogin)
                    dialog.dismiss()
                }
                .show()
        }

    private fun animateButton(button: FloatingActionButton) = binding.apply {
        button.animate().apply {
            duration = 350L
            rotationYBy(180F)
        }.withEndAction {
            button.animate().apply {
                duration = 350L
                rotationYBy(-180F)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}