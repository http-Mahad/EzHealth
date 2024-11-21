package com.hasnain.usermoduleupdated

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hasnain.usermoduleupdated.databinding.ActivityAppointmentBinding
import com.hasnain.usermoduleupdated.models.Appointment
import com.hasnain.usermoduleupdated.models.Profile
import com.hasnain.usermoduleupdated.utils.FirebaseHelper
import java.util.Calendar

class AppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppointmentBinding
    private lateinit var database: DatabaseReference
    private lateinit var usersDatabase: DatabaseReference  // Reference for fetching user CNIC

    private var currentAppointmentId: String? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase references
        database = FirebaseDatabase.getInstance().getReference("appointments")
        usersDatabase = FirebaseDatabase.getInstance().getReference("users") // Reference to users collection

        // Date and Time selection handlers
//        binding.buttonSelectDate.setOnClickListener { openDatePicker() }
//        binding.buttonSelectTime.setOnClickListener { openTimePicker() }
        binding.backArrow.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.imgDate.setOnClickListener { openDatePicker() }
        binding.imgTime.setOnClickListener { openTimePicker() }

        // Book appointment button listener
        binding.buttonBookAppointment.setOnClickListener {
            val medicalIssues = binding.editTextMedicalIssues.text.toString()
            val address = binding.editTextAddress.text.toString()  // Assume you have an EditText for address

            if (selectedDate != null && selectedTime != null && medicalIssues.isNotEmpty() && address.isNotEmpty()) {
                fetchUserCnicAndBookAppointment(medicalIssues, address)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Refresh status button listener
        binding.buttonRefreshStatus.setOnClickListener {
            currentAppointmentId?.let {
                refreshAppointmentStatus(it)
            } ?: Toast.makeText(this, "No appointment to refresh", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch the logged-in user's CNIC by matching their email in the users collection
    private fun fetchUserCnicAndBookAppointment(medicalIssues: String, address: String) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail != null) {
            // Fetch users where the user email matches the current user's email
            usersDatabase.orderByChild("user_email").equalTo(userEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Loop through all matching users and get their CNIC
                            for (userSnapshot in dataSnapshot.children) {
                                val userCnic = userSnapshot.child("user_cnic").getValue(String::class.java)
                                if (userCnic != null) {
                                    // User CNIC found, proceed with booking the appointment
                                    bookAppointment(medicalIssues, address, userCnic)
                                    return
                                }
                            }
                            Toast.makeText(this@AppointmentActivity, "CNIC not found for the user", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@AppointmentActivity, "No matching user found for this email", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@AppointmentActivity, "Error fetching user CNIC: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
        }
    }

    // Book the appointment with all required fields
    private fun bookAppointment(medicalIssues: String, address: String, userCnic: String) {
        val appointmentId = database.push().key ?: return
        val key = generateRandomNumericId()
        fetchUserPhoneNumber { userName,phoneNum ->
            if(phoneNum != null && userName != null){
                val appointment = Appointment(
                    user_test_appointment = medicalIssues,
                    user_cnic_appointment = userCnic,
                    user_time_appointment = selectedTime ?: "",
                    user_name_appointment = userName,
                    user_key_appointment = key,
                    user_date_appointment = selectedDate ?: "",
                    user_status_appointment = "P",  // Default to Pending
                    user_address_appointment = address,
                    user_number_appointment = phoneNum!!.toString()
                )

                // Save appointment data to Firebase
                database.child(appointmentId).setValue(appointment)
                    .addOnSuccessListener {
                        binding.textViewAppointmentStatus.text = "Appointment Status: Pending"
                        binding.textViewAppointmentStatus.visibility = View.VISIBLE
                        binding.buttonRefreshStatus.visibility = View.VISIBLE
                        currentAppointmentId = appointmentId
                        listenForAppointmentStatus(appointmentId)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to book appointment. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this,"Complete Profile", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun generateRandomNumericId(): String {
        return (1..8)
            .map { (0..9).random() }  // This generates a random number between 0 and 9
            .joinToString("")  // Join all the random numbers into a single string
    }


    // Listen for appointment status changes
    private fun listenForAppointmentStatus(appointmentId: String) {
        val appointmentRef = database.child(appointmentId)
        appointmentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointment = snapshot.getValue(Appointment::class.java)
                appointment?.let {
                    updateUIBasedOnStatus(it.user_status_appointment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AppointmentActivity, "Failed to load appointment status.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Refresh appointment status manually
    private fun refreshAppointmentStatus(appointmentId: String) {
        val appointmentRef = database.child(appointmentId)
        appointmentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointment = snapshot.getValue(Appointment::class.java)
                appointment?.let {
                    updateUIBasedOnStatus(it.user_status_appointment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AppointmentActivity, "Failed to refresh status.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Update UI based on appointment status
    @SuppressLint("SetTextI18n")
    private fun updateUIBasedOnStatus(status: String) {
        when (status) {
            "P" -> binding.textViewAppointmentStatus.text = "Appointment Status: Pending"
            "R" -> {
                binding.textViewAppointmentStatus.text = "Appointment Status: Rejected"
                Toast.makeText(this, "Appointment was rejected. Please try booking again.", Toast.LENGTH_SHORT).show()
                binding.buttonRefreshStatus.visibility = View.GONE
            }
            "T" -> binding.textViewAppointmentStatus.text = "Appointment Status: Booked"
            else -> binding.textViewAppointmentStatus.text = "Unknown Status"
        }
    }

    // Open date picker dialog
    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.textViewSelectedDate.text = "Selected Date: $selectedDate"
            binding.etSelectDate.setText(selectedDate)
            binding.etSelectDate.isFocusable = false
            binding.etSelectDate.isClickable = false
        }, year, month, day)

        datePickerDialog.show()
    }
    private fun fetchUserPhoneNumber(callback: (String?,String?) -> Unit) {
        val currentUserEmail = FirebaseHelper.getAuth().currentUser?.email
        if (currentUserEmail != null) {
            FirebaseHelper.profileRef.orderByChild("user_email").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var userName: String? = null
                        var phoneNumber: String? = null
                        if (snapshot.exists()) {
                            for (child in snapshot.children) {
                                val profile = child.getValue(Profile::class.java)
                                if(profile != null) {
                                    userName = profile.user_name

                                    if (profile.user_phone.isNotEmpty()) {
                                        phoneNumber = profile.user_phone
                                        break
                                    }
                                }
                            }
                        }
                        callback(userName,phoneNumber)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null,null) // Handle error case if needed
                    }
                })
        } else {
            callback(null,null)
        }
    }


    // Open time picker dialog
    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun openTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            binding.textViewSelectedTime.text = "Selected Time: $selectedTime"
            binding.etSelectTime.setText(selectedTime)
            binding.etSelectTime.isFocusable = false
            binding.etSelectTime.isClickable = false
        }, hour, minute, true)

        timePickerDialog.show()
    }
}
