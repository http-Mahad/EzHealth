package com.hasnain.usermoduleupdated

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.hasnain.usermoduleupdated.databinding.ActivityPhoneVerificationBinding
import java.util.concurrent.TimeUnit

class PhoneVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneVerificationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Retrieve email, username, and password from intent
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        // Initialize phone verification callbacks
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential, username, email, password)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@PhoneVerificationActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verifId: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = verifId
                Toast.makeText(this@PhoneVerificationActivity, "OTP sent.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonSendOtp.setOnClickListener {
            val phoneNumber = binding.editTextPhone.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendOtp(phoneNumber)
            } else {
                Toast.makeText(this, "Enter phone number.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonVerifyOtp.setOnClickListener {
            val otpCode = binding.editTextOtp.text.toString().trim()
            if (otpCode.isNotEmpty() && verificationId != null) {
                val credential = PhoneAuthProvider.getCredential(verificationId!!, otpCode)
                signInWithPhoneAuthCredential(credential, username, email, password)
            } else {
                Toast.makeText(this, "Enter OTP.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, username: String?, email: String?, password: String?) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val phoneNumber = binding.editTextPhone.text.toString().trim()
                    val intent = Intent(this, CNICSubmissionActivity::class.java)
                    intent.putExtra("username", username)
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    intent.putExtra("phone", phoneNumber)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Verification failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
