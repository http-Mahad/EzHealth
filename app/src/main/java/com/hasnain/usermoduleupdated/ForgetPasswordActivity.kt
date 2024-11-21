package com.hasnain.usermoduleupdated

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hasnain.usermoduleupdated.databinding.ActivityForgetPasswordBinding
import com.hasnain.usermoduleupdated.utils.FirebaseHelper
import org.mindrot.jbcrypt.BCrypt

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseHelper.getAuth()

        // On clicking the reset password button
        binding.btnSendCode.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
        binding.iconDown.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        binding.tvResend.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
                binding.profileMail.visibility=View.VISIBLE
                binding.profileMail.text=email
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        // Set click listener for updating the password
        binding.btnSubmit.setOnClickListener {
            val newSimplePassword = binding.etPassword.text.toString().trim()
             val newPassword=BCrypt.hashpw(newSimplePassword, BCrypt.gensalt())
            if (newPassword.isNotEmpty()) {
                updatePasswordInDatabase(newPassword)
            } else {
                Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()

                    // Start the countdown to show the new password fields after 10 seconds
                    startCountdownToShowPasswordFields()

                } else {
                    Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Function to show the password fields after 10 seconds
    private fun startCountdownToShowPasswordFields() {
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Toast.makeText(this@ForgetPasswordActivity, "Please wait ${millisUntilFinished / 1000} seconds", Toast.LENGTH_SHORT).show()
            }

            override fun onFinish() {
                // Show the EditText and the Button after 10 seconds
                binding.btnSendCode.visibility=View.GONE
                binding.tvNewpass.visibility=View.VISIBLE
                binding.etPassword.visibility = View.VISIBLE
                binding.btnSubmit.visibility = View.VISIBLE
            }
        }.start()
    }

    // Function to update the password in the Realtime Database
    private fun updatePasswordInDatabase(newPassword: String) {
        val currentemail = binding.etEmail.text.toString().trim()
        FirebaseHelper.usersRef.orderByChild("user_email").equalTo(currentemail).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userSnapshot = snapshot.children.first()
                    val userId = userSnapshot.key

                    // Update the password in the Realtime Database
                    userId?.let { id ->
                        FirebaseHelper.usersRef.child(id).child("user_password")
                            .setValue(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this@ForgetPasswordActivity,
                                        "Password updated in the database",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@ForgetPasswordActivity,
                                        "Failed to update password in the database",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(
                        this@ForgetPasswordActivity,
                        "User not found in database",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

}


