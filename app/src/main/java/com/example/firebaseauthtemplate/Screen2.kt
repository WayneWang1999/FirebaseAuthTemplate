package com.example.firebaseauthtemplate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseauthtemplate.databinding.ActivityScreen2Binding
import com.example.firebaseauthtemplate.di.AuthRepository
import com.example.firebaseauthtemplate.models.UserProfile
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class Screen2 : AppCompatActivity() {

 private   lateinit var binding:ActivityScreen2Binding

    // TODO: Auth class property
    // TODO: Firestore class property
    @Inject
    lateinit var authRepository: AuthRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScreen2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Initialize the auth property
        // get user profile data
        loadUserData()
        binding.btnScreen2Logout.setOnClickListener {
            // TODO: Logout user
           logoutUser()
            finish()
        }
        binding.btnUpdateUserProfile.setOnClickListener {
            updateUserData()
        }
    }

    private fun loadUserData() {
        // TODO: Get profile data and show in UI
        val currentUser=authRepository.getCurrentUser()
        if(currentUser==null){
            Log.d("Screen2","No user logged in")
            return
        }


        val uid = currentUser.uid
        // 2. retrieve one document from the userprofiles collection that has
        // this document id = uid
        authRepository.getUserProfile(uid)
            .addOnSuccessListener { document ->
                val profileData = document.toObject(UserProfile::class.java)
                if (profileData != null) {
                    Log.d("Screen2", "Profile data: $profileData")

                    // Populate form fields
                    binding.etHourlyRate.setText(profileData.hourlyRate.toString())
                    binding.swIsManager.isChecked = profileData.isManager
                    binding.etEmail.setText(currentUser.email)
                } else {
                    Log.d("Screen2", "No matching user profile found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Screen2", "Error fetching profile data", exception)
            }

    }

    private fun updateUserData() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            Log.d("TESTING", "No user logged in")
            return
        }

        try {
            val hourlyRate = binding.etHourlyRate.text.toString().toDouble()
            val isManager = binding.swIsManager.isChecked
            val updatedData = UserProfile(id = currentUser.uid, hourlyRate = hourlyRate, isManager = isManager)

            authRepository.updateUserProfile(currentUser.uid, updatedData)
                .addOnSuccessListener {
                    Log.d("Screen2", "Profile successfully updated")
                    Snackbar.make(binding.root, "Profile updated!", Snackbar.LENGTH_SHORT).show()
                    binding.tvResults.text = "Profile updated!"
                }
                .addOnFailureListener { exception ->
                    Log.e("Screen2", "Error updating profile", exception)
                }
        } catch (e: Exception) {
            Log.e("Screen2", "Error parsing input data", e)
            Snackbar.make(binding.root, "Invalid input", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun logoutUser() {
        authRepository.logoutCurrentUser()
        finish()
    }

}