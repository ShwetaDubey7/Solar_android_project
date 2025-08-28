// SYLLABUS POINT 2.1.1: Object declaration (Singleton)
package com.solaro.app.utils

import java.security.MessageDigest

object PasswordHasher {
    // SYLLABUS POINT 2.1.1: Methods of an object
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        // SYLLABUS POINT 1.3.1: Boolean type
        return hashPassword(password) == hashedPassword
    }
}