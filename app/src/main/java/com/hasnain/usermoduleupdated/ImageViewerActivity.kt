package com.hasnain.usermoduleupdated

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hasnain.usermoduleupdated.databinding.ActivityImageViewerBinding
import com.squareup.picasso.Picasso

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgBack.setOnClickListener {
            finish()
        }

        // Get the image URL passed from the adapter
        val imageUrl = intent.getStringExtra("imageUrl")

        // Load the image into the full-screen ImageView
        imageUrl?.let {
            Picasso.get().load(it).into(binding.fullScreenImageView)
        }
    }
}
