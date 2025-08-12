package com.solaro.app.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.solaro.R
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.User
import com.solaro.app.utils.PasswordHasher

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        dbHelper = DatabaseHelper(this)

        val etRegUsername = findViewById<EditText>(R.id.etRegUsername)
        val etRegEmail = findViewById<EditText>(R.id.etRegEmail)
        val etRegPassword = findViewById<EditText>(R.id.etRegPassword)
        val etRegConfirmPassword = findViewById<EditText>(R.id.etRegConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        btnRegister.setOnClickListener {
            val username = etRegUsername.text.toString().trim()
            val email = etRegEmail.text.toString().trim()
            val password = etRegPassword.text.toString().trim()
            val confirmPassword = etRegConfirmPassword.text.toString().trim()

            // Basic validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dbHelper.getUserByUsername(username) != null) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dbHelper.getUserByEmail(email) != null) {
                Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hash the password and create a new user
            val hashedPassword = PasswordHasher.hashPassword(password)
            val newUser = User(
                username = username,
                email = email,
                passwordHash = hashedPassword,
                userType = "user" // All new registrations are regular users
            )

            // Add user to the database
            val result = dbHelper.addUser(newUser)

            if (result != -1L) {
                Toast.makeText(this, "Registration Successful! Please login.", Toast.LENGTH_LONG).show()
                finish() // Close RegisterActivity and return to LoginActivity
            } else {
                Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show()
            }
        }

        tvLoginLink.setOnClickListener {
            // Finishes the current activity and returns to the LoginActivity
            // which is already on the back stack.
            finish()
        }
    }
}
