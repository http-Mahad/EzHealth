package com.hasnain.usermoduleupdated

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hasnain.usermoduleupdated.databinding.ActivityUserProfileBinding
import com.hasnain.usermoduleupdated.models.Profile
import com.hasnain.usermoduleupdated.utils.FirebaseHelper

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var storageReference: StorageReference
    private lateinit var personalRef: DatabaseReference
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private var userEmail: String? = null
    private var personalKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase references
        storageReference = FirebaseStorage.getInstance().reference.child("profile_pics")
        personalRef = FirebaseHelper.profileRef

        // Get current logged-in user email
        userEmail = FirebaseHelper.getAuth().currentUser?.email

        // Display email (non-editable)
        binding.etEmail.text = userEmail


        // Load user profile data if exists
        loadUserProfile()

        // Change profile picture
        binding.changeProfilePicture.setOnClickListener {
            openImagePicker()
        }
        binding.iconDown.setOnClickListener {
            finish()
        }

        // Save profile data
        binding.save.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun loadUserProfile() {
        userEmail?.let { email ->
            personalRef.orderByChild("user_email").equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (data in snapshot.children) {
                                try {
                                    val personal = data.getValue(Profile::class.java)
                                    personal?.let {
                                        personalKey = data.key
                                        binding.profileName.setText(it.user_name)
                                        binding.userPhone.setText(it.user_phone)
                                        binding.profileLocation.setText(it.user_address)

                                        // Load profile picture
                                        Glide.with(this@UserProfileActivity)
                                            .load(it.user_profile_pic)
                                            .into(binding.profile)
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@UserProfileActivity,
                                        "Error parsing profile data: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            // No data found, clear fields if necessary
                            binding.profileName.setText("")
                            binding.userPhone.setText("")
                            binding.profileLocation.setText("")
                            binding.profile.setImageResource(R.drawable.ic_user_placeholder) // A placeholder image
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@UserProfileActivity,
                            "Error loading profile: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }


    private fun saveUserProfile() {
        val name = binding.profileName.text.toString()
        val phone = binding.userPhone.text.toString()
        val address = binding.profileLocation.text.toString()

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            // Upload image only if a new image is selected
            uploadImageToStorage { profilePicUrl ->
                saveProfileToDatabase(name, phone, address, profilePicUrl)
            }
        } else {
            // If no new image, use existing profile picture URL
            loadExistingProfilePicUrl { existingProfilePicUrl ->
                saveProfileToDatabase(name, phone, address, existingProfilePicUrl)
            }
        }
    }

    private fun loadExistingProfilePicUrl(onSuccess: (String) -> Unit) {
        personalKey?.let { key ->
            personalRef.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val existingProfilePicUrl = snapshot.child("user_profile_pic").getValue(String::class.java)
                    onSuccess(existingProfilePicUrl ?: "")
                }

                override fun onCancelled(error: DatabaseError) {
                    onSuccess("") // Return empty string if failed to fetch existing URL
                }
            })
        }
    }


    private fun uploadImageToStorage(onSuccess: (String) -> Unit) {
        val ref = storageReference.child("${System.currentTimeMillis()}.jpg")
        ref.putFile(imageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfileToDatabase(name: String, phone: String, address: String, profilePicUrl: String?) {
        userEmail?.let { email ->
            val profile = Profile(
                user_email = email,
                user_name = name,
                user_profile_pic = profilePicUrl ?: "",  // Only update if new image URL is provided
                user_address = address,
                user_phone = phone
            )

            val isNewUser = personalKey == null
            val key = personalKey ?: personalRef.push().key ?: email.replace(".", "_")
            personalRef.child(key).setValue(profile)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                    if (isNewUser) {
                        val intent = Intent(this, UnderVerificationActivity::class.java)
                        startActivity(intent)
                    }
                    finish() // Finish the current activity
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.profile.setImageURI(imageUri)
        }
    }

}