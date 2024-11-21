package com.hasnain.usermoduleupdated

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.hasnain.usermoduleupdated.databinding.ActivityEmailSignupBinding

class EmailSignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailSignupBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Register button click listener
        binding.signin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val username = binding.username.text.toString().trim()
            val confpass= binding.etConfirmPassword.text.toString().trim()


            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                if(password == confpass){
                    registerUser(email, password, username)
                }
                else{
                    Toast.makeText(this,"Password Doesn't Match",Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Check verification button listener
        binding.btnChkVeri.setOnClickListener {
//            checkEmailVerification()
        }
    }

    private fun registerUser(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            Toast.makeText(this, "Verification email sent!", Toast.LENGTH_SHORT).show()

                            // Show Check Verification button
                            val email = binding.etEmail.text.toString().trim()
                            val username = binding.username.text.toString().trim()
                            val password = binding.etPassword.text.toString().trim()
                            val phone= binding.etPhone.text.toString().trim()


                            val intent = Intent(this, EmailVerificationActivity::class.java)
                            intent.putExtra("username", username)
                            intent.putExtra("email", email)
                            intent.putExtra("password", password)
                            intent.putExtra("phone", phone)
                            startActivity(intent)

                            // Store user data locally for now
//                            saveUserDataAndNavigate(username, email, password)
                        } else {
                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Registration failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun checkEmailVerification() {
        val user = auth.currentUser
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (user.isEmailVerified) {
                    // If email is verified, pass data to PhoneVerificationActivity
                    val email = binding.etEmail.text.toString().trim()
                    val username = binding.username.text.toString().trim()
                    val password = binding.etPassword.text.toString().trim()

                    val intent = Intent(this, CNICSubmissionActivity::class.java)
                    intent.putExtra("username", username)
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Failed to refresh user data. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
