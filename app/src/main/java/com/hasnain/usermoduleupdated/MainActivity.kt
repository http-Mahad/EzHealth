package com.hasnain.usermoduleupdated

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hasnain.usermoduleupdated.databinding.ActivityMainBinding
import com.hasnain.usermoduleupdated.fragments.HomeFragment
import com.hasnain.usermoduleupdated.fragments.TestsAvailableFragment
import com.hasnain.usermoduleupdated.fragments.ProfileFragment
import com.hasnain.usermoduleupdated.fragments.ReportsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)


        // Set initial fragment (HomeFragment) when activity is created
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())  // Load HomeFragment as default
        }

        // Handle bottom navigation item clicks
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            resetIconsToDefault()
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_home_selected)
                    true
                }
                R.id.nav_reports -> {
                    loadFragment(ReportsFragment())
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_reports_selected)
                    true
                }
                R.id.nav_notifications -> {
                    loadFragment(TestsAvailableFragment())
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_tests_selected)
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    item.icon = ContextCompat.getDrawable(this, R.drawable.ic_profile_selected)
                    true
                }
                else -> false
            }
        }



    }
    private fun resetIconsToDefault() {
        binding.bottomNavigation.menu.findItem(R.id.nav_home).icon = ContextCompat.getDrawable(this, R.drawable.ic_home)
        binding.bottomNavigation.menu.findItem(R.id.nav_reports).icon = ContextCompat.getDrawable(this, R.drawable.ic_reports)
        binding.bottomNavigation.menu.findItem(R.id.nav_notifications).icon = ContextCompat.getDrawable(this, R.drawable.ic_tests_selected)
        binding.bottomNavigation.menu.findItem(R.id.nav_profile).icon = ContextCompat.getDrawable(this, R.drawable.ic_user)
    }

    // Function to load fragments into the FrameLayout
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)  // Correct ID for FrameLayout
            .commit()
    }
}
