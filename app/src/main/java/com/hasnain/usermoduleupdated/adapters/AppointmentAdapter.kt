package com.hasnain.usermoduleupdated.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hasnain.usermoduleupdated.R
import com.hasnain.usermoduleupdated.models.Appointment

class AppointmentAdapter(private val appointmentsList: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val testAppointment: TextView? = itemView.findViewById(R.id.tvTestAppointment)
        val dateAppointment: TextView? = itemView.findViewById(R.id.tvDateAppointment)
        val timeAppointment: TextView? = itemView.findViewById(R.id.tvTimeAppointment)
        val addressAppointment: TextView? = itemView.findViewById(R.id.tvAddressAppointment)
        val statusAppointment: TextView? = itemView.findViewById(R.id.textViewAppointmentStatus)
        val acceptedStatus:View?=itemView.findViewById(R.id.statusSubmittedLine)
        val rejectedStatus:View?=itemView.findViewById(R.id.statusShortlistedLine)
        val completedStatus:View?=itemView.findViewById(R.id.statusOfferLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointmentsList[position]

        holder.testAppointment?.text = appointment.user_test_appointment ?: "N/A"
        holder.dateAppointment?.text = appointment.user_date_appointment ?: "N/A"
        holder.timeAppointment?.text = appointment.user_time_appointment ?: "N/A"
        holder.addressAppointment?.text = appointment.user_address_appointment ?: "N/A"
        holder.statusAppointment?.text = when (appointment.user_status_appointment) {
            "P" -> "Status: Pending"
            "T" -> "Status: Confirmed"
            "R" -> "Status: Rejected"
            "C" -> "Status: Completed"
            else -> "Status: Unknown"
        }
        holder.acceptedStatus?.setBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (appointment.user_status_appointment == "T") R.color.Darkblue else R.color.gray
            )
        )

        // Set the color for rejectedStatus (red if status is "R", otherwise grey)
        holder.rejectedStatus?.setBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (appointment.user_status_appointment == "R") R.color.red else R.color.gray
            )
        )

        // Set the color for completedStatus (blue if status is "C", otherwise grey)
        holder.completedStatus?.setBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (appointment.user_status_appointment == "C") R.color.Darkblue else R.color.gray
            )
        )

    }

    override fun getItemCount(): Int {
        return appointmentsList.size
    }
}

