package com.solaro.app.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.utils.SessionManager
import com.solaro.databinding.ActivityRequestMaintenanceBinding

@Suppress("DEPRECATION")
class RequestMaintenanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestMaintenanceBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSubmitTicket.setOnClickListener {
            submitTicket()
        }
    }

    private fun submitTicket() {
        val description = binding.etMaintDescription.text.toString().trim()
        if (description.isEmpty()) {
            Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = sessionManager.getUserId()
        if (userId == -1L) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Add maintenance ticket without an image path
        val success = dbHelper.addMaintenanceTicket(userId, description, null)

        if (success != -1L) {
            Toast.makeText(this, "Maintenance ticket submitted successfully!", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to submit ticket.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}