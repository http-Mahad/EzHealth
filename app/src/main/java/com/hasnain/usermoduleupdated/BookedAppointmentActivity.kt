package com.hasnain.usermoduleupdated

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hasnain.usermoduleupdated.databinding.ActivityBookedAppointmentBinding
import com.hasnain.usermoduleupdated.models.Appointment
import com.hasnain.usermoduleupdated.utils.FirebaseHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hasnain.usermoduleupdated.adapters.AppointmentAdapter

class BookedAppointmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookedAppointmentBinding
    private val appointmentsList = mutableListOf<Appointment>()
    private lateinit var appointmentAdapter: AppointmentAdapter
    private val swipeThreshold = 0.5f // Define a threshold for the swipe distance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookedAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgBack.setOnClickListener {
            finish()
        }
        // Initialize RecyclerView
        appointmentAdapter = AppointmentAdapter(appointmentsList)
        binding.recyclerViewAppointments.apply {
            layoutManager = LinearLayoutManager(this@BookedAppointmentActivity)
            adapter = appointmentAdapter
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val appointment = appointmentsList[position]

                // Remove item from RecyclerView
                appointmentsList.removeAt(position)
                appointmentAdapter.notifyItemRemoved(position)

                // Delete item from Firebase database
                val databaseReference = FirebaseDatabase.getInstance().getReference("appointments")
                val appointmentKey =
                    appointment.user_key_appointment
                val appointmentcnic=appointment.user_cnic_appointment// Ensure the Appointment model has a `key` property for unique ID

                if (appointmentKey != null) {
                    FirebaseHelper.appointmentsRef.orderByChild("user_key_appointment").equalTo(appointmentKey)
                        .get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                for (child in snapshot.children) {
                                    child.ref.removeValue().addOnSuccessListener {
                                        Toast.makeText(
                                            this@BookedAppointmentActivity,
                                            "Appointment deleted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            this@BookedAppointmentActivity,
                                            "Failed to delete appointment",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@BookedAppointmentActivity,
                                    "No matching appointment found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this@BookedAppointmentActivity,
                                "Failed to query database",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val maxSwipeDistance = -itemView.width * swipeThreshold

                // Restrict swipe distance for left swipe
                val restrictedDX = when {
                    dX < maxSwipeDistance -> maxSwipeDistance // Restrict left swipe to a threshold
                    dX > 0 -> 0f // Prevent moving item forward on right swipe
                    else -> dX
                }

                // Draw the swipe layout when swiping left (dX < 0)
                if (restrictedDX < 0) {
                    val inflater = LayoutInflater.from(this@BookedAppointmentActivity)
                    val swipeLayout = inflater.inflate(R.layout.swipe_reveal_layout, null)

                    // Measure and layout the swipe layout view to match the item's size
                    swipeLayout.measure(
                        View.MeasureSpec.makeMeasureSpec(itemView.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(itemView.height, View.MeasureSpec.EXACTLY)
                    )
                    swipeLayout.layout(0, 0, itemView.width, itemView.height)

                    // Draw the swipe layout on the canvas
                    c.save()
                    c.translate(itemView.right + restrictedDX, itemView.top.toFloat())
                    swipeLayout.draw(c)
                    c.restore()
                }

                // Continue with the default item swipe behavior but with restricted swipe distance
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    restrictedDX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewAppointments)

        // Fetch user email and CNIC to get appointments
        fetchUserEmail { userEmail ->
            fetchUserCnic(userEmail) { userCnic ->
                fetchAppointments(userCnic)
            }
        }
    }

    private fun fetchUserEmail(callback: (String) -> Unit) {
        val user = FirebaseHelper.getAuth().currentUser
        user?.email?.let {
            callback(it)
        } ?: run {
            Toast.makeText(this, "No user logged in or email not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserCnic(userEmail: String, callback: (String) -> Unit) {
        FirebaseHelper.usersRef.orderByChild("user_email").equalTo(userEmail).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userCnic = snapshot.children.firstOrNull()?.child("user_cnic")?.value as? String
                    userCnic?.let {
                        callback(it)
                    } ?: run {
                        Toast.makeText(this, "CNIC not found for the user", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No matching user found for this email", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error fetching user CNIC: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchAppointments(userCnic: String) {
        FirebaseHelper.appointmentsRef.orderByChild("user_cnic_appointment").equalTo(userCnic)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    appointmentsList.clear()
                    if (snapshot.exists()) {
                        for (appointmentSnapshot in snapshot.children) {
                            val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                            appointment?.let {
                                appointmentsList.add(it)
                            }
                        }
                        appointmentAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@BookedAppointmentActivity, "No appointments found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@BookedAppointmentActivity, "Error fetching appointments: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
