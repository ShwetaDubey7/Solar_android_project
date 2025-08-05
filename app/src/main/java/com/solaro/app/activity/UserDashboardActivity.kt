package com.solaro.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.solaro.LoginActivity
import com.solaro.R
import com.solaro.utils.SessionManager

class  UserDashboardActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        sessionManager = SessionManager(this)

        val tvWelcomeUser = findViewById<TextView>(R.id.tvWelcomeUser)

        // Get the username passed from LoginActivity
        val username = intent.getStringExtra("USER_NAME")
        tvWelcomeUser.text = "Welcome, ${username ?: "User"}!"

        val btnRequestInstallation = findViewById<Button>(R.id.btnRequestInstallation)
        btnRequestInstallation.setOnClickListener {
            val intent = Intent(this, RequestInstallationActivity::class.java)
            startActivity(intent)
    }

    // Add a simple options menu for the logout button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            sessionManager.logoutUser()
            val intent = Intent(this, LoginActivity::class.java)
            // Clear the activity stack so the user cannot go back to the dashboard
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}