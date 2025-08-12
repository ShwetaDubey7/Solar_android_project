package com.solaro.app.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages the user's login session using SharedPreferences.
 * This allows the app to remember the user across app restarts.
 */
class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SolaroAppPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        private const val IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_TYPE = "userType"
        private const val KEY_USERNAME = "username"
    }

    /**
     * Creates a login session for the user.
     */
    fun createLoginSession(userId: Long, userType: String, username: String) {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putLong(KEY_USER_ID, userId)
        editor.putString(KEY_USER_TYPE, userType)
        editor.putString(KEY_USERNAME, username)
        editor.commit()
    }

    /**
     * Checks if a user is currently logged in.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    /**
     * Retrieves the logged-in user's ID.
     */
    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L) // Return -1 if not found
    }

    /**
     * Retrieves the logged-in user's type ("user" or "admin").
     */
    fun getUserType(): String? {
        return prefs.getString(KEY_USER_TYPE, null)
    }

    /**
     * Retrieves the logged-in user's username.
     */
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    /**
     * Clears the session data, effectively logging the user out.
     */
    fun logoutUser() {
        editor.clear()
        editor.commit()
    }
}