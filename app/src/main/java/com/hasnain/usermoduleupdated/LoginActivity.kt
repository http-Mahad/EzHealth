package com.hasnain.usermoduleupdated

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hasnain.usermoduleupdated.databinding.ActivityLoginBinding
import com.hasnain.usermoduleupdated.models.User
import com.hasnain.usermoduleupdated.utils.FirebaseHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Check if the user is already logged in (session exists)
        if (isUserLoggedIn()) {
            val userEmail = getUserEmailFromSession()
            if (userEmail != null) {
                fetchUserData(userEmail)
            }
        }

        binding.signin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

//        binding.buttonResendVerification.setOnClickListener {
//            resendVerificationEmail()
//        }

        binding.signup.setOnClickListener {
            startActivity(Intent(this, EmailSignupActivity::class.java))
        }
        binding.forgetPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
    }

    // Function to log in the user
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        saveSession(email)  // Save session when login is successful
                        fetchUserData(email)
                    } else {
                        Toast.makeText(this, "Please verify your email address.", Toast.LENGTH_SHORT).show()
                        resendVerificationEmail()
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Function to fetch user data from the database
    private fun fetchUserData(email: String) {
        FirebaseHelper.usersRef.orderByChild("user_email").equalTo(email).get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val userData = dataSnapshot.children.first().getValue(User::class.java)
                    userData?.let {
                        navigateBasedOnUserData(it)
                    }
                } else {
                    // User not found in database
                    Toast.makeText(this, "User not found in database.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, CNICSubmissionActivity::class.java))
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Failed to retrieve user data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateBasedOnUserData(userData: User) {
        when {

            userData.user_cnic.isEmpty() -> {
                // Navigate to CNICActivity
                startActivity(Intent(this, CNICSubmissionActivity::class.java))
            }
            userData.user_account_status == "P" -> {
                // Navigate to UnderVerificationActivity
                startActivity(Intent(this, UnderVerificationActivity::class.java))
            }
            userData.user_account_status == "F" -> {
                // Display rejection message
                Toast.makeText(this, "Account Verification Rejected", Toast.LENGTH_LONG).show()
            }
            userData.user_account_status == "T" -> {
                // Navigate to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    // Resend email verification
    private fun resendVerificationEmail() {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
            if (verifyTask.isSuccessful) {
                Toast.makeText(this, "Verification email sent!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Session Management Functions

    private fun saveSession(email: String) {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_email", email)
            apply() // Save the email to session
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPref.contains("user_email") // Check if session exists
    }

    private fun getUserEmailFromSession(): String? {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPref.getString("user_email", null) // Get email from session
    }

    private fun clearSession() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear() // Clear session data
            apply()
        }
    }
}
