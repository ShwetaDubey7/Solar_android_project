package com.solaro.app.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.User
import com.solaro.app.utils.PasswordHasher
import com.solaro.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.btnRegister.setOnClickListener {
            handleRegistration()
        }

        binding.tvLoginLink.setOnClickListener {
            finish() // Go back to the login screen
        }
    }

    private fun handleRegistration() {
        val username = binding.etRegUsername.text.toString().trim()
        val email = binding.etRegEmail.text.toString().trim()
        val password = binding.etRegPassword.text.toString().trim()
        val confirmPassword = binding.etRegConfirmPassword.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (dbHelper.getUserByUsername(username) != null) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
            return
        }
        if (dbHelper.getUserByEmail(email) != null) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show()
            return
        }

        val hashedPassword = PasswordHasher.hashPassword(password)
        val newUser = User(
            username = username,
            email = email,
            passwordHash = hashedPassword,
            userType = "user"
        )

        val success = dbHelper.addUser(newUser)
        if (success != -1L) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}