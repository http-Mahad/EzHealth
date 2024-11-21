package com.hasnain.usermoduleupdated

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hasnain.usermoduleupdated.databinding.ActivityReportAccessBinding
import com.squareup.picasso.Picasso

class ReportAccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportAccessBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportAccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        // When the button is clicked, fetch the user's email, then check the user report
        binding.btnFetchCnic.setOnClickListener {
            fetchUserEmail { userEmail ->
                fetchUserCnic(userEmail) { userCnic ->
                    fetchReport(userCnic, false) // Fetch user report
                }
            }
        }
        binding.btnNavHome.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        // When the second button is clicked, fetch the parental report
        binding.btnFetchParentalCnic.setOnClickListener {
            fetchUserEmail { userEmail ->
                fetchUserCnic(userEmail) { userCnic ->
                    fetchReport(userCnic, true) // Fetch parental report
                }
            }
        }
    }

    // Fetch the current user's email from Firebase Auth
    private fun fetchUserEmail(callback: (String) -> Unit) {
        val user = auth.currentUser
        user?.let {
            val userEmail = it.email
            if (!userEmail.isNullOrEmpty()) {
                callback(userEmail)
            } else {
                Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch the CNIC of the user whose email matches the authenticated email
    private fun fetchUserCnic(userEmail: String, callback: (String) -> Unit) {
        // Search for the user by email in the users collection
        database.orderByChild("user_email").equalTo(userEmail).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // If a user is found, get the user_cnic field
                for (userSnapshot in snapshot.children) {
                    val userCnic = userSnapshot.child("user_cnic").value as String?
                    userCnic?.let {
                        callback(it) // Pass the CNIC back to the callback
                        return@addOnSuccessListener
                    }
                }
                Toast.makeText(this, "CNIC not found for the user", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No matching user found for this email", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching user CNIC: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch the report based on the CNIC
    private fun fetchReport(userCnic: String, isParental: Boolean) {
        val reportsRef = FirebaseDatabase.getInstance().getReference("reports")
        val queryField = if (isParental) "user_report_parent_cnic" else "user_report_cnic"

        // Search the reports collection for a matching CNIC
        reportsRef.orderByChild(queryField).equalTo(userCnic)
            .get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (reportSnapshot in snapshot.children) {
                        val reportUrl = reportSnapshot.child("user_report_url").value as String?
                        reportUrl?.let {
                            Toast.makeText(this, if (isParental) "Parental Report Found!" else "User Report Found!", Toast.LENGTH_SHORT).show()
                            displayReportImage(it) // Display the report image
                            return@addOnSuccessListener
                        }
                    }
                } else {
                    Toast.makeText(this, if (isParental) "No parental report found" else "No user report found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error fetching report: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Display the report image using Picasso
    private fun displayReportImage(imageUrl: String) {
        val imageView: ImageView = binding.reportImageView
        Picasso.get().load(imageUrl).into(imageView)
    }
}
