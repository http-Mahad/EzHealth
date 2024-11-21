package com.hasnain.usermoduleupdated.adapters

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hasnain.usermoduleupdated.ImageViewerActivity
import com.hasnain.usermoduleupdated.R
import com.hasnain.usermoduleupdated.databinding.ItemReportBinding
import com.hasnain.usermoduleupdated.models.Report
import com.squareup.picasso.Picasso

class ReportAdapter(
    private val reportList: List<Report>
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reportImageView: ImageView = itemView.findViewById(R.id.reportImageView)
        val reportNameTextView: TextView = itemView.findViewById(R.id.tv_report_name)
        val reportDateTextView: TextView = itemView.findViewById(R.id.tv_report_date)
        val downloadButton: Button = itemView.findViewById(R.id.downloadButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]

        // Load the image using Picasso
        Picasso.get().load(report.user_report_url).into(holder.reportImageView)

        // Set report name and date
        holder.reportNameTextView.text = report.user_report_name
        holder.reportDateTextView.text = report.user_report_date

        // Open image in full screen on click
        holder.reportImageView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ImageViewerActivity::class.java).apply {
                putExtra("imageUrl", report.user_report_url)
            }
            context.startActivity(intent)
        }

        // Download image on button click
        holder.downloadButton.setOnClickListener {
            val context = holder.itemView.context
            downloadImage(context, report.user_report_url)
        }
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    private fun downloadImage(context: Context, imageUrl: String) {
        val request = DownloadManager.Request(Uri.parse(imageUrl))
        request.setDescription("Downloading report image")
        request.setTitle("Report Image")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "report_image.jpg")

        // Get download service and enqueue the request
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
    }
}
