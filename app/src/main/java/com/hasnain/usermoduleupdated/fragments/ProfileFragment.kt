package com.hasnain.usermoduleupdated.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.hasnain.usermoduleupdated.AppointmentActivity
import com.hasnain.usermoduleupdated.BookedAppointmentActivity
import com.hasnain.usermoduleupdated.LoginActivity
import com.hasnain.usermoduleupdated.R
import com.hasnain.usermoduleupdated.ReceiptHandlingActivity
import com.hasnain.usermoduleupdated.UserProfileActivity
import com.hasnain.usermoduleupdated.databinding.FragmentHomeBinding
import com.hasnain.usermoduleupdated.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        _binding!!.tvBookedAppointment.setOnClickListener{
            val intent = Intent(requireActivity(), BookedAppointmentActivity::class.java)
            startActivity(intent)
        }
        _binding!!.tvReceipt.setOnClickListener{
            val intent = Intent(requireActivity(), ReceiptHandlingActivity::class.java)
            startActivity(intent)
        }
        _binding!!.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()

        }
        _binding!!.tvEditProfile.setOnClickListener {
            startActivity(Intent(requireActivity(),UserProfileActivity::class.java))

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun clearSession() {
        val sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear() // Clear session data
            apply()
        }
    }
    private fun redirectToLoginScreen() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        startActivity(intent)
         // Close the current activity
    }
    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout_confirmation, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogView)

        // Configure dialog properties
        dialog.setCancelable(true)

        // Set up buttons
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)

        btnCancel.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog when "No" is clicked
        }

        btnConfirm.setOnClickListener {
            clearSession()
            redirectToLoginScreen()
            dialog.dismiss() // Dismiss the dialog when "Yes" is clicked
        }

        dialog.show()
    }



}