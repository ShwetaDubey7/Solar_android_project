package com.solaro.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.solaro.app.activity.AdminDashboardActivity
import com.solaro.app.activity.UserDashboardActivity
import com.solaro.app.utils.SessionManager

/**
 * The main entry point of the application after the splash screen.
 * This activity's sole purpose is to check the user's login status
 * and route them to the appropriate screen. It has no UI itself.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the session manager to check login state
        val sessionManager = SessionManager(this)

        // Check if the user is logged in
        if (sessionManager.isLoggedIn()) {
            // User is logged in, navigate to the correct dashboard
            val userType = sessionManager.getUserType()
            val username = sessionManager.getUsername()
            navigateToHomepage(userType, username)
        } else {
            // User is not logged in, navigate to the Login screen
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Finish MainActivity so the user cannot navigate back to it
        finish()
    }

    /**
     * Navigates to the correct dashboard based on the user's role.
     * @param userType The role of the user ("user" or "admin").
     * @param username The username to display in the welcome message.
     */
    private fun navigateToHomepage(userType: String?, username: String?) {
        val intent = when (userType) {
            "admin" -> Intent(this, AdminDashboardActivity::class.java)
            "user" -> Intent(this, UserDashboardActivity::class.java)
            else -> {
                // As a fallback in case of corrupted session data, go to Login
                Intent(this, LoginActivity::class.java)
            }
        }
        // Pass the username for the welcome message
        intent.putExtra("USER_NAME", username)
        startActivity(intent)
    }
}
