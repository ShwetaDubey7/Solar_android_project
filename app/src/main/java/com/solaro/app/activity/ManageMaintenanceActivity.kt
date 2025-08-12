package com.solaro.app.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.solaro.R
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.MaintenanceTicket
import java.io.File

class ManageMaintenanceActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var maintenanceAdapter: MaintenanceAdapter
    private var ticketList = ArrayList<MaintenanceTicket>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_maintenance)

        dbHelper = DatabaseHelper(this)

        val rvMaintenance = findViewById<RecyclerView>(R.id.rvMaintenance)
        rvMaintenance.layoutManager = LinearLayoutManager(this)

        maintenanceAdapter = MaintenanceAdapter(ticketList) {
            loadTickets()
        }
        rvMaintenance.adapter = maintenanceAdapter
    }

    override fun onResume() {
        super.onResume()
        loadTickets()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadTickets() {
        val allTickets = dbHelper.getAllMaintenanceTickets()
        ticketList.clear()
        ticketList.addAll(allTickets)
        maintenanceAdapter.notifyDataSetChanged()
    }
}

class MaintenanceAdapter(
    private val tickets: List<MaintenanceTicket>,
    private val refreshData: () -> Unit
) : RecyclerView.Adapter<MaintenanceAdapter.MaintenanceViewHolder>() {

    class MaintenanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val description: TextView = view.findViewById(R.id.tvMaintDescription)
        val status: TextView = view.findViewById(R.id.tvMaintStatus)
        val image: ImageView = view.findViewById(R.id.ivMaintImage)
        val updateButton: Button = view.findViewById(R.id.btnUpdateMaintStatus)
        val deleteButton: Button = view.findViewById(R.id.btnDeleteMaint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_maintenance, parent, false)
        return MaintenanceViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        val ticket = tickets[position]
        val context = holder.itemView.context
        val dbHelper = DatabaseHelper(context)

        holder.description.text = ticket.description
        holder.status.text = "Status: ${ticket.status}"

        if (!ticket.imagePath.isNullOrEmpty()) {
            holder.image.visibility = View.VISIBLE
            holder.image.setImageURI(Uri.fromFile(File(ticket.imagePath)))
        } else {
            holder.image.visibility = View.GONE
        }

        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Ticket")
                .setMessage("Are you sure you want to delete this ticket?")
                .setPositiveButton("Yes") { _, _ ->
                    dbHelper.deleteMaintenanceTicket(ticket.id)
                    Toast.makeText(context, "Ticket deleted.", Toast.LENGTH_SHORT).show()
                    refreshData()
                }
                .setNegativeButton("No", null)
                .show()
        }

        holder.updateButton.setOnClickListener {
            val statusOptions = arrayOf("Open", "Assigned", "Resolved", "Closed")
            AlertDialog.Builder(context)
                .setTitle("Update Status")
                .setItems(statusOptions) { _, which ->
                    val newStatus = statusOptions[which]
                    dbHelper.updateMaintenanceStatus(ticket.id, newStatus)
                    Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                    refreshData()
                }
                .show()
        }
    }

    override fun getItemCount() = tickets.size
}
