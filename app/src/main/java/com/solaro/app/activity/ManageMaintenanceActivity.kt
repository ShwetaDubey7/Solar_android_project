package com.solaro.app.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.solaro.app.adapter.MaintenanceAdapter
import com.solaro.app.database.DatabaseHelper
import com.solaro.databinding.ActivityManageMaintenanceBinding

@Suppress("DEPRECATION")
class ManageMaintenanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageMaintenanceBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var maintenanceAdapter: MaintenanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadMaintenanceTickets()
    }

    private fun setupRecyclerView() {
        maintenanceAdapter = MaintenanceAdapter(mutableListOf(), dbHelper) {
            // Refresh the list after an item is deleted or updated
            loadMaintenanceTickets()
        }
        binding.rvMaintenance.apply {
            layoutManager = LinearLayoutManager(this@ManageMaintenanceActivity)
            adapter = maintenanceAdapter
        }
    }

    private fun loadMaintenanceTickets() {
        val ticketList = dbHelper.getAllMaintenanceTickets()
        maintenanceAdapter.updateTickets(ticketList)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}