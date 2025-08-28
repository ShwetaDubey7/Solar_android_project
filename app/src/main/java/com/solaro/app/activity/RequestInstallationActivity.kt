package com.solaro.app.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.utils.SessionManager
import com.solaro.databinding.ActivityRequestInstallationBinding

@Suppress("DEPRECATION")
class RequestInstallationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestInstallationBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestInstallationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSubmitRequest.setOnClickListener {
            submitRequest()
        }
    }

    private fun submitRequest() {
        val address = binding.etInstallationAddress.text.toString().trim()
        if (address.isEmpty()) {
            Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = sessionManager.getUserId()
        if (userId == -1L) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        val success = dbHelper.addInstallationRequest(userId, address)
        if (success != -1L) {
            Toast.makeText(this, "Request submitted successfully!", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to submit request.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}