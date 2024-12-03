package com.example.firebaseauthtemplate.di

import com.example.firebaseauthtemplate.models.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore  // Only use one instance for FireStore
) {

    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }

    fun signupUser(
        email: String,
        password: String,
        hourlyRate: Double,
        isManager: Boolean,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val data = mapOf(
                            "hourlyRate" to hourlyRate,
                            "isManager" to isManager
                        )
                        firestore.collection("userProfiles")
                            .document(userId)
                            .set(data)
                            .addOnSuccessListener { onResult(true, null) }
                            .addOnFailureListener { ex -> onResult(false, ex.localizedMessage) }
                    } else {
                        onResult(false, "Failed to get user ID")
                    }
                } else {
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }

    fun logoutCurrentUser() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getUserProfile(uid: String): Task<DocumentSnapshot> {
        return firestore.collection("userprofiles").document(uid).get()  // Use firestore here
    }

    fun updateUserProfile(uid: String, userProfile: UserProfile): Task<Void> {
        return firestore.collection("userprofiles").document(uid).set(userProfile)
    }

}
