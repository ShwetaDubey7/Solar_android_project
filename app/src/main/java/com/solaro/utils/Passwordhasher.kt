package com.solaro.utils

class Passwordhasher {
    package com.yourcompany.solaro.utils

    import at.favre.lib.crypto.bcrypt.BCrypt // We need to add a dependency for this!

    object `PasswordHasher.kt` {
        // This is a dummy implementation. In a real app, use a strong hashing library like BCrypt.
        // For a mini-project, you can use a simpler approach initially, but be aware of security.

        // To use BCrypt, add this to your app/build.gradle (Module: app) dependencies:
        // implementation 'at.favre.lib.crypto:bcrypt:0.9.0'
        // Don't forget to Sync Project with Gradle Files after adding.

        private val BCRYPT_VERSION = BCrypt.Version.VERSION_2A
        private val BCRYPT_COST = 10 // Higher cost means more secure but slower hashing

        fun hashPassword(password: String): String {
            return BCrypt.with(BCRYPT_VERSION).hashToString(BCRYPT_COST, password.toCharArray())
        }

        fun verifyPassword(password: String, hashedPassword: String): Boolean {
            return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword.toCharArray()).verified
        }
    }
}