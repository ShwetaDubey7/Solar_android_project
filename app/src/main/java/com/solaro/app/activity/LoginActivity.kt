package com.solaro.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.solaro.R
import com.solaro.app.activity.AdminDashboardActivity
import com.solaro.app.activity.RegisterActivity
import com.solaro.app.activity.UserDashboardActivity
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.utils.PasswordHasher
import com.solaro.app.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            val userType = sessionManager.getUserType()
            val username = sessionManager.getUsername()
            navigateToHomepage(userType, username)
            finish()
            return
        }

        setContentView(R.layout.activity_login)
        dbHelper = DatabaseHelper(this)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val usernameOrEmail = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var user = dbHelper.getUserByUsername(usernameOrEmail)
            if (user == null) {
                user = dbHelper.getUserByEmail(usernameOrEmail)
            }

            if (user != null) {
                if (PasswordHasher.verifyPassword(password, user.passwordHash)) {
                    sessionManager.createLoginSession(user.id, user.userType, user.username)
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    navigateToHomepage(user.userType, user.username)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToHomepage(userType: String?, username: String?) {
        val intent = when (userType) {
            "admin" -> Intent(this, AdminDashboardActivity::class.java)
            "user" -> Intent(this, UserDashboardActivity::class.java)
            else -> {
                Toast.makeText(this, "Unknown user role.", Toast.LENGTH_LONG).show()
                return
            }
        }
        intent.putExtra("USER_NAME", username)
        startActivity(intent)
    }
}