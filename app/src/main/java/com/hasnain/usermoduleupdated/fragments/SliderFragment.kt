package com.hasnain.usermoduleupdated.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hasnain.usermoduleupdated.R
import com.hasnain.usermoduleupdated.databinding.FragmentSliderBinding




class SliderFragment : Fragment() {
    private lateinit var binding: FragmentSliderBinding

    companion object {
        // Factory method to create a new instance of the fragment
        fun newInstance(imageRes: Int, title: String, description: String): SliderFragment {
            val fragment = SliderFragment()
            val bundle = Bundle()
            // Put the image resource as an Int, and the title and description as Strings
            bundle.putInt("image", imageRes)
            bundle.putString("title", title)
            bundle.putString("description", description)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSliderBinding.inflate(inflater, container, false)

        // Retrieve the arguments passed to the fragment
        val imageRes = arguments?.getInt("image")
        val title = arguments?.getString("title")
        val description = arguments?.getString("description")

        // Set data to views
        imageRes?.let { binding.imageSlider.setImageResource(it) }
        binding.slideText.text = title
        binding.slideSubtext.text = description

        return binding.root
    }
}