// SYLLABUS POINT 2.1.1: Basics of classes and objects in Kotlin
package com.solaro.app.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val PREF_NAME = "SolaroSession"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"
    private val KEY_USER_ID = "userId"
    private val KEY_USERNAME = "username"
    private val KEY_USER_TYPE = "userType" // "admin" or "user"

    // SYLLABUS POINT 2.1.1: Properties of a class
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    // SYLLABUS POINT 2.1.1: Methods of a class
    fun createLoginSession(userId: Long, username: String, userType: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putLong(KEY_USER_ID, userId) // SYLLABUS POINT 1.3.1: Long type
        editor.putString(KEY_USERNAME, username) // SYLLABUS POINT 1.3.2: String type
        editor.putString(KEY_USER_TYPE, userType)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        // SYLLABUS POINT 1.3.1: Boolean type
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // SYLLABUS POINT 1.3.1: 'val' for immutable return
    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L)
    }

    // SYLLABUS POINT 1.3.2: Nullable variable (String?)
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun getUserType(): String? {
        return prefs.getString(KEY_USER_TYPE, null)
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()
    }
}