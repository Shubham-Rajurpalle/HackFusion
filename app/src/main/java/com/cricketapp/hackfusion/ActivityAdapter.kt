package com.cricketapp.hackfusion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cricketapp.hackfusion.databinding.ItemActivityBinding

class ActivityAdapter(
    private var activityList: List<Activity>
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(private val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activity: Activity) {
            // Bind the text views
            binding.titleText.text = activity.title
            binding.descriptionText.text = activity.description

            // Load the image using Glide
            Glide.with(binding.root.context)
                .load(activity.imageUrl)
                .error(R.drawable.activity_bg)  // Optional: error image in case of failure
                .into(binding.backgroundImage)  // ImageView where image will be loaded
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(activityList[position])
    }

    override fun getItemCount() = activityList.size

    fun updateList(newList: List<Activity>) {
        activityList = newList
        notifyDataSetChanged()
    }
}
