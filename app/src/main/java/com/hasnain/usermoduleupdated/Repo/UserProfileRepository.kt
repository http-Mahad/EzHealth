package com.hasnain.usermoduleupdated.Repo

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hasnain.usermoduleupdated.R
import com.hasnain.usermoduleupdated.models.Profile
import com.hasnain.usermoduleupdated.utils.FirebaseHelper

class UserProfileRepository {

    fun fetchUserProfile(
        includeName: Boolean = true,
        includePhone: Boolean = true,
        includeEmail: Boolean = true,
        includeImage: Boolean = true,
        imageView: ImageView? = null, // Pass ImageView for loading image
        callback: (Map<String, Any?>) -> Unit
    ) {
        val currentUserEmail = FirebaseHelper.getAuth().currentUser?.email

        if (currentUserEmail != null) {
            FirebaseHelper.profileRef.orderByChild("user_email").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val profileData = mutableMapOf<String, Any?>()

                        if (snapshot.exists()) {
                            for (child in snapshot.children) {
                                val profile = child.getValue(Profile::class.java)
                                profile?.let {
                                    // Conditionally add values based on user request
                                    if (includeName) profileData["user_name"] = it.user_name
                                    if (includePhone && it.user_phone.isNotEmpty()) profileData["user_phone"] = it.user_phone
                                    if (includeEmail) profileData["user_email"] = it.user_email

                                    // Load the image only if it's requested and the URL is valid
                                    if (includeImage && !it.user_profile_pic.isNullOrEmpty()) {
                                        loadImage(it.user_profile_pic, imageView)
                                    }
                                }
                            }
                        }

                        // Call callback with the fetched profile data
                        callback(profileData)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Log error or show user feedback
                        callback(emptyMap()) // Return empty data in case of error
                    }
                })
        } else {
            // Handle case where no user is authenticated
            callback(emptyMap()) // Return empty data if no user email
        }
    }

    // Function to load the image into the provided ImageView
    private fun loadImage(imageUrl: String?, imageView: ImageView?) {
        if (imageView == null || imageUrl.isNullOrEmpty()) {
            imageView?.setImageResource(R.drawable.profile_image) // Set a placeholder if the URL is null/empty
            return
        }

        val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(imageView.context)
                .load(uri)
                .into(imageView)
        }.addOnFailureListener {
            // Handle error during image loading
            imageView.setImageResource(R.drawable.profile_image) // Placeholder image if loading fails
        }
    }

}
