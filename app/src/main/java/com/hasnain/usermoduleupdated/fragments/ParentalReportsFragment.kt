package com.hasnain.usermoduleupdated.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hasnain.usermoduleupdated.adapters.ReportAdapter
import com.hasnain.usermoduleupdated.databinding.FragmentParentalReportsBinding
import com.hasnain.usermoduleupdated.models.Report
import com.hasnain.usermoduleupdated.utils.FirebaseHelper

class ParentalReportsFragment : Fragment() {
    private var _binding: FragmentParentalReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var reportAdapter: ReportAdapter
    private val reportList = mutableListOf<Report>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParentalReportsBinding.inflate(inflater, container, false)

        setupRecyclerView()

        // Fetch reports when the fragment is opened
        fetchUserEmail { userEmail ->
            fetchUserCnic(userEmail) { userCnic ->
                fetchReports(userCnic)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Set up RecyclerView with the adapter
    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter(reportList)
        binding.recyclerViewParentalReports.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reportAdapter
        }
    }

    // Fetch the current user's email from Firebase Auth
    private fun fetchUserEmail(callback: (String) -> Unit) {
        val user = FirebaseHelper.getAuth().currentUser
        user?.let {
            val userEmail = it.email
            if (!userEmail.isNullOrEmpty()) {
                callback(userEmail)
            } else {
                Toast.makeText(requireContext(), "User email not found", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch the CNIC of the user whose email matches the authenticated email
    private fun fetchUserCnic(userEmail: String, callback: (String) -> Unit) {
        FirebaseHelper.usersRef.orderByChild("user_email").equalTo(userEmail).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userCnic = userSnapshot.child("user_cnic").value as String?
                        userCnic?.let {
                            callback(it) // Pass the CNIC back to the callback
                            return@addOnSuccessListener
                        }
                    }
                    Toast.makeText(requireContext(), "CNIC not found for the user", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No matching user found for this email", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error fetching user CNIC: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Fetch the parental reports based on the CNIC
    private fun fetchReports(userCnic: String) {
        val queryField = "user_report_parent_cnic"

        FirebaseHelper.reportsRef.orderByChild(queryField).equalTo(userCnic)
            .get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    reportList.clear()
                    for (reportSnapshot in snapshot.children) {
                        val report = reportSnapshot.getValue(Report::class.java)
                        report?.let {
                            reportList.add(it)
                        }
                    }
                    reportAdapter.notifyDataSetChanged()
                } else {
                    showNoReportsFound()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error fetching reports: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Show "No reports found" message
    private fun showNoReportsFound() {
        binding.recyclerViewParentalReports.visibility = View.GONE
        binding.noReportsTextView.visibility = View.VISIBLE
    }
}
