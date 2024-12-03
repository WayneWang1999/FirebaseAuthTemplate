package com.example.firebaseauthtemplate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseauthtemplate.databinding.ActivityMainBinding
import com.example.firebaseauthtemplate.di.AuthRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Click handlers
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            authRepository.loginUser(email, password) { success, message ->
                if (success) {
                    binding.tvResults.text = "SUCCESS: $email logged in."
                    navigateToScreen2()
                } else {
                    binding.tvResults.text = "ERROR: $message"
                }
            }
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val hourlyRate = binding.etHourlyRate.text.toString().toDoubleOrNull() ?: 0.0
            val isManager = binding.swIsManager.isChecked

            authRepository.signupUser(email, password, hourlyRate, isManager) { success, message ->
                if (success) {
                    binding.tvResults.text = "New user created."
                } else {
                    binding.tvResults.text = "ERROR: $message"
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            authRepository.logoutCurrentUser()
            binding.tvResults.text = "User logged out."
        }

        binding.btnCheckLogin.setOnClickListener {
            if (authRepository.isUserLoggedIn()) {
                binding.tvResults.text = "User is logged in."
                navigateToScreen2()
            } else {
                binding.tvResults.text = "No user is logged in."
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (authRepository.isUserLoggedIn()) {
            navigateToScreen2()
        }
    }

    private fun navigateToScreen2() {
        val intent = Intent(this, Screen2::class.java)
        startActivity(intent)
    }
}
