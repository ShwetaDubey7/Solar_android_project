package com.solaro.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.solaro.app.utils.SessionManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // This must be called before super.onCreate() for the new Splash Screen API
        installSplashScreen()

        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        // Determine the next activity based on login status
        val intent = if (sessionManager.isLoggedIn()) {
            val userType = sessionManager.getUserType()
            val username = sessionManager.getUsername()

            val targetActivity = when (userType?.lowercase()) {
                "admin" -> AdminDashboardActivity::class.java
                "user" -> UserDashboardActivity::class.java
                else -> LoginActivity::class.java // Fallback in case of unknown user type
            }
            // Create an intent for the target activity and pass the username
            Intent(this, targetActivity).apply {
                putExtra(LoginActivity.EXTRA_USER_NAME, username)
            }
        } else {
            // If not logged in, go to LoginActivity
            Intent(this, LoginActivity::class.java)
        }

        // Start the determined activity and finish the splash screen
        startActivity(intent)
        finish()
    }
}