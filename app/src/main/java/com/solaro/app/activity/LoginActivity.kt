package com.app.solaro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
// FIX: Assuming your project structure follows conventions, these utilities would be in these packages.
// Update these imports to match your actual file locations.
import com.app.solaro.activity.RegisterActivity // FIX: Corrected import path
import com.solaro.AdminDashboardActivity
import com.solaro.app.activity.UserDashboardActivity
import com.solaro.utils.Passwordhasher
import com.solaro.utils.SessionManager


class LoginActivity : AppCompatActivity() {

    // FIX: Uncommented the DatabaseHelper variable.
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // Check if user is already logged in BEFORE setting the content view
        if (sessionManager.isLoggedIn()) {
            navigateToHomepage(sessionManager.getUserType())
            finish() // Close login activity to prevent user from going back to it
            return   // Stop further execution of onCreate
        }

        // FIX: setContentView MUST be called to load the UI. This is the most important fix.
        setContentView(R.layout.activity_login)

        // FIX: Initialize the DatabaseHelper after setting the content view.
        dbHelper = DatabaseHelper(this)

        // Initialize views
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username/email and password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val user = dbHelper.getUserByUsername(username)

            if (user != null) {
                // Verify password using the PasswordHasher utility
                if (Passwordhasher.verifyPassword(password, user.passwordHash)) {
                    // Create session upon successful password verification
                    sessionManager.createLoginSession(user.id, user.userType, user.username)
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    navigateToHomepage(user.userType, user.username)
                    finish() // Close login activity
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToHomepage(userType: String?, username: String?) {
        val intent = when (userType) {
            // Uncomment these lines now that the activities exist
            "admin" -> Intent(this, AdminDashboardActivity::class.java)
            "user" -> Intent(this, UserDashboardActivity::class.java)
            else -> {
                Toast.makeText(this, "Unknown user role.", Toast.LENGTH_LONG).show()
                return // Don't navigate if the role is unknown
            }
        }
        // Pass the username to the next activity for the welcome message
        intent.putExtra("USER_NAME", username)
        startActivity(intent)
    }
}
