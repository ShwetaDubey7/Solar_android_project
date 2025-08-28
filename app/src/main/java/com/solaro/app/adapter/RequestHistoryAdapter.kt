package com.solaro.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.solaro.R
import com.solaro.app.models.RequestHistoryItem
import com.solaro.databinding.ListItemRequestHistoryBinding

class RequestHistoryAdapter(
    private var items: MutableList<RequestHistoryItem>
) : RecyclerView.Adapter<RequestHistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: ListItemRequestHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ListItemRequestHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.binding.tvRequestDescription.text = item.description
        holder.binding.tvRequestStatus.text = item.status
        holder.binding.tvRequestType.text = "${item.type} Request"

        if (item.type == "Installation") {
            holder.binding.ivRequestIcon.setImageResource(R.drawable.ic_add_home)
        } else {
            holder.binding.ivRequestIcon.setImageResource(R.drawable.ic_build)
        }

        // Change status color based on status text
        val statusColor = when (item.status.lowercase()) {
            "completed", "resolved" -> R.color.status_green
            "in progress", "assigned" -> R.color.status_blue
            "cancelled" -> R.color.status_red
            else -> R.color.md_theme_primary // Default "Pending" or "Open"
        }
        holder.binding.tvRequestStatus.background.setTint(ContextCompat.getColor(context, statusColor))
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<RequestHistoryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}