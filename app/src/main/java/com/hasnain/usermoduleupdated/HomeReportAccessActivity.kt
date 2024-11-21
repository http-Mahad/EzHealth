package com.hasnain.usermoduleupdated

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ReportFragment
import com.hasnain.usermoduleupdated.fragments.ReportsFragment

class HomeReportAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_report_access)

        // Check if the fragment is not already added to the fragment manager
        if (savedInstanceState == null) {
            // Create the fragment instance
            val reportFragment = ReportsFragment()

            // Begin a fragment transaction to add the fragment dynamically
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

            // Add the fragment to the container
            transaction.replace(R.id.fragment_container, reportFragment)
            transaction.commit() // Commit the transaction
        }
    }
}