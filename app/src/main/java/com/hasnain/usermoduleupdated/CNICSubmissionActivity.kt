package com.hasnain.usermoduleupdated

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hasnain.usermoduleupdated.databinding.ActivityCnicsubmissionBinding
import com.hasnain.usermoduleupdated.models.User
import org.mindrot.jbcrypt.BCrypt

class CNICSubmissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCnicsubmissionBinding
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var auth: FirebaseAuth

    // Firebase references
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCnicsubmissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Retrieve all previous data from intent
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val simplepassword = intent.getStringExtra("password")
        val phone = intent.getStringExtra("phone")
        val password= BCrypt.hashpw(simplepassword, BCrypt.gensalt())

        // Set click listeners for selecting image and submitting CNIC
        binding.buttonSelectImage.setOnClickListener {
            openFileChooser()
        }

        binding.buttonUploadCnic.setOnClickListener {
            val cnic = binding.editTextCnic.text.toString().trim()

            if (cnic.isNotEmpty() && imageUri != null) {
                uploadCnicData(cnic, imageUri!!, username, email, password, phone)
            } else {
                Toast.makeText(this, "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to open image chooser
    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handle image result from the image chooser
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            binding.imageViewCnic.setImageURI(imageUri)
        }
    }

    // Function to upload CNIC data
    private fun uploadCnicData(
        cnic: String,
        imageUri: Uri,
        username: String?,
        email: String?,
        password: String?,
        phone: String?
    ) {
        // Ensure user is authenticated
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = storage.child("cnic_images/${System.currentTimeMillis()}.jpg")

        // Upload the CNIC image to Firebase Storage
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Create User object using the provided User data class
                    val user = User(
                        user_name = username ?: "",
                        user_email = email ?: "",
                        user_password = password ?: "",
                        user_cnic = cnic,
                        user_cnic_img_url = uri.toString(),
                        user_phone = phone?:"",
                        user_account_status = "P" // Under verification
                    )

                    // Save user data in Firebase Realtime Database
                    database.child(userId).setValue(user)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "User data uploaded successfully.", Toast.LENGTH_SHORT).show()
                                // Navigate to UnderVerificationActivity
                                startActivity(Intent(this, UserProfileActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
