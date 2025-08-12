package com.solaro.app.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.solaro.R
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.utils.SessionManager

class RequestInstallationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_installation)

        val dbHelper = DatabaseHelper(this)
        val sessionManager = SessionManager(this)
        val etAddress = findViewById<EditText>(R.id.etInstallationAddress)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitRequest)

        btnSubmit.setOnClickListener {
            val address = etAddress.text.toString().trim()
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get the ID of the currently logged-in user
            val userId = sessionManager.getUserId()
            if (userId == -1L) {
                // This should not happen if the user is properly logged in
                Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Add the request to the database
            val result = dbHelper.addInstallationRequest(userId, address)

            if (result != -1L) {
                Toast.makeText(this, "Request submitted successfully!", Toast.LENGTH_LONG).show()
                finish() // Close this activity and return to the dashboard
            } else {
                Toast.makeText(this, "Failed to submit request. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
