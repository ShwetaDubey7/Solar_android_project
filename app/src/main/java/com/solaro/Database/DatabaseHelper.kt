package com.app.solaro.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.solaro.models.User.User

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Increment this if you change the schema
        private const val DATABASE_NAME = "Solaro.db"

        // --- Users Table ---
        private const val TABLE_USERS = "users"
        private const val KEY_USER_ID_COL = "id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD_HASH = "password_hash"
        private const val KEY_USER_TYPE = "user_type"

        // --- Installations Table ---
        private const val TABLE_INSTALLATIONS = "installations"
        private const val KEY_INSTALL_ID = "id"
        private const val KEY_INSTALL_USER_ID = "user_id"
        private const val KEY_ADDRESS = "address"
        private const val KEY_STATUS = "status"

        // --- Maintenance Table ---
        private const val TABLE_MAINTENANCE = "maintenance"
        private const val KEY_MAINT_ID = "id"
        private const val KEY_MAINT_USER_ID = "user_id"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_IMAGE_PATH = "image_path"
        // Re-using KEY_STATUS for this table
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Users Table
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT UNIQUE,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PASSWORD_HASH + " TEXT,"
                + KEY_USER_TYPE + " TEXT" + ")")
        db?.execSQL(createUsersTable)

        // Create Installations Table
        val createInstallationsTable = ("CREATE TABLE " + TABLE_INSTALLATIONS + "("
                + KEY_INSTALL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_INSTALL_USER_ID + " INTEGER,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_STATUS + " TEXT" + ")")
        db?.execSQL(createInstallationsTable)

        // Create Maintenance Table
        val createMaintenanceTable = ("CREATE TABLE " + TABLE_MAINTENANCE + "("
                + KEY_MAINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_MAINT_USER_ID + " INTEGER,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_IMAGE_PATH + " TEXT,"
                + KEY_STATUS + " TEXT" + ")")
        db?.execSQL(createMaintenanceTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INSTALLATIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MAINTENANCE")
        onCreate(db)
    }

    // =====================================================================================
    // ## User Functions ##
    // =====================================================================================

    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_USERNAME, user.username)
        contentValues.put(KEY_EMAIL, user.email)
        contentValues.put(KEY_PASSWORD_HASH, user.passwordHash)
        contentValues.put(KEY_USER_TYPE, user.userType)
        val success = db.insert(TABLE_USERS, null, contentValues)
        db.close()
        return success
    }

    fun getUserByUsername(username: String): User? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $KEY_USERNAME = ?", arrayOf(username))
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_USER_ID_COL)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD_HASH)),
                userType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_TYPE))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    fun getUserByEmail(email: String): User? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $KEY_EMAIL = ?", arrayOf(email))
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_USER_ID_COL)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD_HASH)),
                userType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_TYPE))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    // =====================================================================================
    // ## Installation Functions ##
    // =====================================================================================

    fun addInstallationRequest(userId: Int, address: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_INSTALL_USER_ID, userId)
        contentValues.put(KEY_ADDRESS, address)
        contentValues.put(KEY_STATUS, "Pending") // Default status
        val success = db.insert(TABLE_INSTALLATIONS, null, contentValues)
        db.close()
        return success
    }

    // =====================================================================================
    // ## Maintenance Functions ##
    // =====================================================================================

    fun addMaintenanceTicket(userId: Long, description: String, imagePath: String?): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_MAINT_USER_ID, userId)
        contentValues.put(KEY_DESCRIPTION, description)
        contentValues.put(KEY_IMAGE_PATH, imagePath)
        contentValues.put(KEY_STATUS, "Open") // Default status
        val success = db.insert(TABLE_MAINTENANCE, null, contentValues)
        db.close()
        return success
    }
}