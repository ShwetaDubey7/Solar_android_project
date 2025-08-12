package com.solaro.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.solaro.R
import com.solaro.app.LoginActivity
import com.solaro.app.utils.SessionManager

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)
        sessionManager = SessionManager(this)

        val tvWelcomeUser = findViewById<TextView>(R.id.tvWelcomeUser)
        val username = intent.getStringExtra("USER_NAME")
        tvWelcomeUser.text = "Welcome, ${username ?: "User"}!"

        // Button to open the installation request screen
        val btnRequestInstallation = findViewById<Button>(R.id.btnRequestInstallation)
        btnRequestInstallation.setOnClickListener {
            startActivity(Intent(this, RequestInstallationActivity::class.java))
        }

        // Button to open the maintenance request screen
        val btnRequestMaintenance = findViewById<Button>(R.id.btnRequestMaintenance)
        btnRequestMaintenance.setOnClickListener {
            // We will create RequestMaintenanceActivity in a later step
            // For now, this can be a placeholder
            Toast.makeText(this, "Maintenance feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    // Creates the top-right options menu (for logout)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    // Handles clicks on the options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            sessionManager.logoutUser()
            val intent = Intent(this, LoginActivity::class.java)
            // Clear the activity history so the user can't go back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
