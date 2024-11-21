package com.hasnain.usermoduleupdated

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.hasnain.usermoduleupdated.adapters.ReceiptPagerAdapter
import com.hasnain.usermoduleupdated.databinding.ActivityReceiptHandlingBinding

class ReceiptHandlingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptHandlingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptHandlingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up ViewPager with the adapter
        val receiptPagerAdapter = ReceiptPagerAdapter(this)
        binding.viewPager.adapter = receiptPagerAdapter

        // Connect the TabLayout with the ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Own Receipt"
                1 -> "Parental Receipt"
                else -> null
            }
        }.attach()
    }
}
