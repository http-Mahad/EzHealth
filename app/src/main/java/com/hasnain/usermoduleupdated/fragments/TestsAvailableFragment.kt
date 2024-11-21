package com.hasnain.usermoduleupdated.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.hasnain.usermoduleupdated.databinding.FragmentTestsAvailableBinding
import com.hasnain.usermoduleupdated.models.Tests
import com.hasnain.usermoduleupdated.utils.FirebaseHelper
import com.hasnain.usermoduleupdated.adapters.TestsAdapter

class TestsAvailableFragment : Fragment() {
    private var _binding: FragmentTestsAvailableBinding? = null
    private val binding get() = _binding!!
    private val testsList = ArrayList<Tests>()
    private lateinit var adapter: TestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestsAvailableBinding.inflate(inflater, container, false)

        // Set up RecyclerView
        binding.recyclerViewTests.layoutManager = LinearLayoutManager(requireContext())
        adapter = TestsAdapter(testsList)
        binding.recyclerViewTests.adapter = adapter

        // Fetch tests from Firebase
        fetchTestsFromFirebase()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchTestsFromFirebase() {
        FirebaseHelper.testsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                testsList.clear() // Clear list before adding items

                for (testSnapshot in snapshot.children) {
                    val test = testSnapshot.getValue(Tests::class.java)
                    if (test != null) {
                        testsList.add(test)
                    }
                }

                adapter.notifyDataSetChanged() // Notify adapter that data has changed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
            }
        })
    }
}
