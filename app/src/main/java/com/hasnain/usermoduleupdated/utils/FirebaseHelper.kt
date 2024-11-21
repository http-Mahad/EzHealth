package com.hasnain.usermoduleupdated.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object FirebaseHelper {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // Database references
    val usersRef: DatabaseReference = database.getReference("users")
    val reportsRef: DatabaseReference = database.getReference("reports")
    val appointmentsRef: DatabaseReference = database.getReference("appointments")
    val testsRef: DatabaseReference = database.getReference("tests")
    val profileRef: DatabaseReference = database.getReference("profile")

    // Firebase Auth instance
    fun getAuth(): FirebaseAuth {
        return auth
    }

    // Firebase Storage instance
    fun getStorage(): FirebaseStorage {
        return storage
    }
}
