package com.solaro.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.MaintenanceTicket
import com.solaro.databinding.ListItemMaintenanceBinding

class MaintenanceAdapter(
    private var ticketList: MutableList<MaintenanceTicket>,
    private val dbHelper: DatabaseHelper,
    private val onListChanged: () -> Unit
) : RecyclerView.Adapter<MaintenanceAdapter.MaintenanceViewHolder>() {

    inner class MaintenanceViewHolder(val binding: ListItemMaintenanceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val binding = ListItemMaintenanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MaintenanceViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        val ticket = ticketList[position]
        val context = holder.itemView.context
        holder.binding.tvMaintDescription.text = ticket.description
        holder.binding.tvMaintStatus.text = "Status: ${ticket.status}"

        holder.binding.btnDeleteMaint.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Ticket")
                .setMessage("Are you sure you want to delete this maintenance ticket?")
                .setPositiveButton("Delete") { _, _ ->
                    if (dbHelper.deleteMaintenanceTicket(ticket.id) > 0) {
                        Toast.makeText(context, "Ticket deleted", Toast.LENGTH_SHORT).show()
                        onListChanged()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        holder.binding.btnUpdateMaintStatus.setOnClickListener {
            val statusOptions = arrayOf("Open", "Assigned", "Resolved", "Closed")
            AlertDialog.Builder(context)
                .setTitle("Update Status")
                .setItems(statusOptions) { _, which ->
                    val newStatus = statusOptions[which]
                    if (dbHelper.updateMaintenanceStatus(ticket.id, newStatus) > 0) {
                        Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                        onListChanged()
                    }
                }
                .show()
        }
    }

    override fun getItemCount(): Int = ticketList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTickets(newList: List<MaintenanceTicket>) {
        ticketList.clear()
        ticketList.addAll(newList)
        notifyDataSetChanged()
    }
}
