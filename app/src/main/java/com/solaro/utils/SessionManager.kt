package com.solaro.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val PREF_NAME = "SolaroSession"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"
    private val KEY_USER_ID = "userId"
    private val KEY_USER_TYPE = "userType" // "admin" or "user"

    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()

    fun createLoginSession(userId: Int, userType: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_USER_TYPE, userType)
        editor.apply() // Apply changes asynchronously
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): Int {
        return sharedPref.getInt(KEY_USER_ID, -1) // -1 if not found
    }

    fun getUserType(): String? {
        return sharedPref.getString(KEY_USER_TYPE, null)
    }

    fun logoutUser() {
        editor.clear() // Clear all data from shared preferences
        editor.apply()
    }
}