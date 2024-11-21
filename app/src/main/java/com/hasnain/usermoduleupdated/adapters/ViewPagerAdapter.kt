package com.hasnain.usermoduleupdated.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hasnain.usermoduleupdated.models.SliderItem
import com.hasnain.usermoduleupdated.R

class ViewPagerAdapter(private val sliderItems: List<SliderItem>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slider_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val sliderItem = sliderItems[position]
        holder.imageView.setImageResource(sliderItem.image)
        holder.textView.text = sliderItem.text
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }
}
