package com.example.stateflow

import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.stateflow.databinding.ActivityMainBinding
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar

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

            btnLogin.setOnClickListener {
                setViewModelLoginMethod()
            }
            btnClear.setOnClickListener {
                clearScreen().also {
                    Snackbar.make(binding.root, "Cleared Screen", Snackbar.LENGTH_SHORT)
                        .setAction("OK"){_ -> Unit } .show()
                }
            }
        }
        determineLoginState()
    }

    private fun setViewModelLoginMethod() {
        binding.apply {
            viewModel.login(
                etLogin.text.toString(),
                etPassword.text.toString()
            )
        }
    }

    private fun determineLoginState() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collect {
                when (it) {
                    is MainViewModel.LoginState.Success -> {
                        Snackbar.make(binding.root, "Success!!!", Snackbar.LENGTH_SHORT).show()
                        binding.apply {
                            progressBar.visibility = View.GONE
                            textInputLayoutLogin.boxBackgroundColor = getColor(R.color.white)
                            textInputLayoutPassword.boxBackgroundColor = getColor(R.color.white)
                            textInputLayoutPassword.helperText = getColor(R.color.white).toString()
                            textInputLayoutPassword.helperText = getColor(R.color.white).toString()
                        }
                    }

                    is MainViewModel.LoginState.Failure -> {
                        Snackbar.make(binding.root, it.errorMessage, Snackbar.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }

                    is MainViewModel.LoginState.Loading -> {
                        Snackbar.make(binding.root, "Loading State", Snackbar.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is MainViewModel.LoginState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }

                    is MainViewModel.LoginState.Empty -> {
                        Unit
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}