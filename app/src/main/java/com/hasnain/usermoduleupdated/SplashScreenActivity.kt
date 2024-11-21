package com.hasnain.usermoduleupdated

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Delay of 2 seconds to show splash screen
        android.os.Handler().postDelayed({
            if (isUserLoggedIn()) {
                // User is logged in, navigate to MainActivity
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            } else {
                // User is not logged in, navigate to LoginActivity
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }
            // Close SplashScreenActivity after navigating
            finish()
        }, 2000) // Delay of 2 seconds (2000ms)
    }

    // Function to check if the user session exists
    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPref.contains("user_email") // Check if session exists
    }

    // Function to get user email from session (if exists)
    private fun getUserEmailFromSession(): String? {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPref.getString("user_email", null) // Get email from session
    }

    // Function to clear session (if needed)
    private fun clearSession() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear() // Clear session data
            apply()
        }
    }
}