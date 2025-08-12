package com.solaro.app.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.solaro.R
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.Installation

class ManageInstallationsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var installationsAdapter: InstallationAdapter
    private var installationList = ArrayList<Installation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_installations)

        dbHelper = DatabaseHelper(this)

        val rvInstallations = findViewById<RecyclerView>(R.id.rvInstallations)
        rvInstallations.layoutManager = LinearLayoutManager(this)

        // The adapter is passed a function { loadInstallations() }
        // which it can call to refresh the list after an item is changed.
        installationsAdapter = InstallationAdapter(installationList) {
            loadInstallations()
        }
        rvInstallations.adapter = installationsAdapter
    }

    override fun onResume() {
        super.onResume()
        // Load or refresh the data every time the activity is shown
        loadInstallations()
    }

    private fun loadInstallations() {
        val allInstallations = dbHelper.getAllInstallations()
        installationList.clear()
        installationList.addAll(allInstallations)
        // Notify the adapter that the data set has changed
        installationsAdapter.notifyDataSetChanged()
    }
}

/**
 * Adapter to connect the Installation data to the RecyclerView.
 */
class InstallationAdapter(
    private val installations: MutableList<Installation>,
    private val refreshData: () -> Unit // A function to be called to refresh the list
) : RecyclerView.Adapter<InstallationAdapter.InstallationViewHolder>() {

    // Holds the views for a single list item.
    class InstallationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addressTextView: TextView = view.findViewById(R.id.tvInstallAddress)
        val statusTextView: TextView = view.findViewById(R.id.tvInstallStatus)
        val idTextView: TextView = view.findViewById(R.id.tvInstallId)
        val updateButton: Button = view.findViewById(R.id.btnUpdateStatus)
        val deleteButton: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstallationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_installation, parent, false)
        return InstallationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstallationViewHolder, position: Int) {
        val installation = installations[position]
        val context = holder.itemView.context
        val dbHelper = DatabaseHelper(context)

        holder.addressTextView.text = installation.address
        holder.statusTextView.text = "Status: ${installation.status}"
        holder.idTextView.text = "Request ID: #${installation.id}"

        // Set listener for the delete button
        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Request")
                .setMessage("Are you sure you want to delete this request?")
                .setPositiveButton("Yes") { _, _ ->
                    val success = dbHelper.deleteInstallation(installation.id)
                    if (success > 0) {
                        Toast.makeText(context, "Request deleted.", Toast.LENGTH_SHORT).show()
                        refreshData() // Refresh the list
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Set listener for the update status button
        holder.updateButton.setOnClickListener {
            val statusOptions = arrayOf("Pending", "In Progress", "Completed", "Cancelled")
            AlertDialog.Builder(context)
                .setTitle("Update Status")
                .setItems(statusOptions) { _, which ->
                    val newStatus = statusOptions[which]
                    val success = dbHelper.updateInstallationStatus(installation.id, newStatus)
                    if (success > 0) {
                        Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                        refreshData() // Refresh the list
                    }
                }
                .show()
        }
    }

    override fun getItemCount() = installations.size
}
