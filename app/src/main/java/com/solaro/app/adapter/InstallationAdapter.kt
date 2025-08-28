package com.solaro.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.Installation
import com.solaro.databinding.ListItemInstallationBinding

class InstallationAdapter(
    private var installationList: MutableList<Installation>,
    private val dbHelper: DatabaseHelper,
    private val onListChanged: () -> Unit
) : RecyclerView.Adapter<InstallationAdapter.InstallationViewHolder>() {

    inner class InstallationViewHolder(val binding: ListItemInstallationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstallationViewHolder {
        val binding = ListItemInstallationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InstallationViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: InstallationViewHolder, position: Int) {
        val installation = installationList[position]
        val context = holder.itemView.context
        holder.binding.tvInstallAddress.text = installation.address
        holder.binding.tvInstallStatus.text = "Status: ${installation.status}"

        holder.binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Request")
                .setMessage("Are you sure you want to delete this installation request?")
                .setPositiveButton("Delete") { _, _ ->
                    if (dbHelper.deleteInstallation(installation.id) > 0) {
                        Toast.makeText(context, "Request deleted", Toast.LENGTH_SHORT).show()
                        onListChanged()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        holder.binding.btnUpdateStatus.setOnClickListener {
            val statusOptions = arrayOf("Pending", "In Progress", "Completed", "Cancelled")
            AlertDialog.Builder(context)
                .setTitle("Update Status")
                .setItems(statusOptions) { _, which ->
                    val newStatus = statusOptions[which]
                    if (dbHelper.updateInstallationStatus(installation.id, newStatus) > 0) {
                        Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                        onListChanged()
                    }
                }
                .show()
        }
    }

    override fun getItemCount(): Int = installationList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateInstallations(newList: List<Installation>) {
        installationList.clear()
        installationList.addAll(newList)
        notifyDataSetChanged()
    }
}