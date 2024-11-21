package com.hasnain.usermoduleupdated.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hasnain.usermoduleupdated.databinding.ItemTestBinding
import com.hasnain.usermoduleupdated.models.Tests

class TestsAdapter(private val testsList: List<Tests>) : RecyclerView.Adapter<TestsAdapter.TestsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestsViewHolder {
        val binding = ItemTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TestsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestsViewHolder, position: Int) {
        val test = testsList[position]
        holder.bind(test)
    }

    override fun getItemCount(): Int {
        return testsList.size
    }

    inner class TestsViewHolder(private val binding: ItemTestBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(test: Tests) {
            binding.tvTestName.text = test.test_name
            binding.tvTestPrice.text = "Price: ${test.test_price}"
            binding.tvTestRequirements.text = "Requirements: ${test.test_requirments}"
        }
    }
}
