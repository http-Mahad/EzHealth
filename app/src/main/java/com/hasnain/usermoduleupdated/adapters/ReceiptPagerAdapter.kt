package com.hasnain.usermoduleupdated.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hasnain.usermoduleupdated.ReceiptHandlingActivity
import com.hasnain.usermoduleupdated.fragments.OwnReceiptFragment
import com.hasnain.usermoduleupdated.fragments.ParentalReceiptFragment

class ReceiptPagerAdapter(activity: ReceiptHandlingActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OwnReceiptFragment()
            1 -> ParentalReceiptFragment()
            else -> OwnReceiptFragment()
        }
    }
}
