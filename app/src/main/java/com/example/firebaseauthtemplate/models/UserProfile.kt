package com.example.firebaseauthtemplate.models

import com.google.firebase.firestore.DocumentId

data class UserProfile (
    @DocumentId
    var id:String = "",
    var hourlyRate:Double = 0.0,
    @JvmField
    var isManager:Boolean = false
)