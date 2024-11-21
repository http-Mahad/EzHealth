package com.hasnain.usermoduleupdated.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import com.hasnain.usermoduleupdated.adapters.NestedFragmentAdapter

import com.hasnain.usermoduleupdated.databinding.FragmentHomeBinding
import com.hasnain.usermoduleupdated.databinding.FragmentReportsBinding


class ReportsFragment : Fragment() {
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)

        val adapter = NestedFragmentAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        // Set up TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "User Reports"
                1 -> "Parental Reports"
                else -> null
            }
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
