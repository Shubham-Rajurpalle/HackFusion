package com.cricketapp.hackfusion.Notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cricketapp.hackfusion.R
import com.cricketapp.hackfusion.databinding.ItemActivityBinding

class NotificationAdapter(
    private var notificationList: List<Notification>
) : RecyclerView.Adapter<NotificationAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(private val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {

            binding.titleText.text = notification.title
            binding.descriptionText.text = notification.description

            Glide.with(binding.root.context)
                .load(notification.imageUrl)
                .error(R.drawable.dummy_image)
                .into(binding.backgroundImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount() = notificationList.size

    fun updateList(newList: List<Notification>) {
        notificationList = newList
        notifyDataSetChanged()
    }
}
