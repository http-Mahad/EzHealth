package com.hasnain.usermoduleupdated.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hasnain.usermoduleupdated.databinding.ItemReceiptBinding
import com.hasnain.usermoduleupdated.models.Report

class ReceiptAdapter(
    private val reportList: List<Report>,
    private val isParentalReport: Boolean // To determine if patient name should be shown
) : RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder>() {

    inner class ReceiptViewHolder(private val binding: ItemReceiptBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: Report) {
            binding.tvReportName.text = report.user_report_name
            binding.tvReportDate.text = report.user_report_date
            binding.tvReportPrice.text = report.user_report_price

            // Show patient name only for parental reports
            if (isParentalReport) {
                binding.tvPatientName.visibility = View.VISIBLE
                binding.tvPatientName.text = report.user_report_patient_name
            } else {
                binding.tvPatientName.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val binding = ItemReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val report = reportList[position]
        holder.bind(report)
    }

    override fun getItemCount(): Int = reportList.size
}
