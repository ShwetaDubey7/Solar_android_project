package com.solaro.app.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.utils.PasswordHasher
import com.solaro.app.utils.SessionManager
import com.solaro.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER_NAME = "extra_user_name"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            return
        }

        val user = dbHelper.getUserByUsername(username)

        if (user != null && PasswordHasher.verifyPassword(password, user.passwordHash)) {
            sessionManager.createLoginSession(user.id, user.username, user.userType)
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

            val intent = if (user.userType.equals("admin", ignoreCase = true)) {
                Intent(this, AdminDashboardActivity::class.java)
            } else {
                Intent(this, UserDashboardActivity::class.java)
            }
            intent.putExtra(EXTRA_USER_NAME, user.username)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }
}