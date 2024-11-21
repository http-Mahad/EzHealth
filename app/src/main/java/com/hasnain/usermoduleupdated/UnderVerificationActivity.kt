package com.hasnain.usermoduleupdated

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hasnain.usermoduleupdated.databinding.ActivityUnderVerificationBinding
import com.hasnain.usermoduleupdated.models.User

class UnderVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnderVerificationBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnderVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Display a message to the user
        binding.text.text = "Your account is under verification. Please wait for approval."

        auth = FirebaseAuth.getInstance()

        binding.buttonCheckVerification.setOnClickListener {
            checkVerificationStatus()
        }
    }

    private fun checkVerificationStatus() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            database.child(userId).get().addOnSuccessListener { dataSnapshot ->
                val userData = dataSnapshot.getValue(User::class.java)
                if (userData != null) {
                    when (userData.user_account_status) {
                        "P" -> {
                            // Account is still under verification
                            Toast.makeText(this, "Account is under verification.", Toast.LENGTH_SHORT).show()
                        }
                        "R" -> {
                            // Verification rejected
                            Toast.makeText(this, "Verification rejected. Contact admin for more information.", Toast.LENGTH_SHORT).show()
                        }
                        "T" -> {
                            // Navigate to MainActivity if verified
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve verification status.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "User is not logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}
