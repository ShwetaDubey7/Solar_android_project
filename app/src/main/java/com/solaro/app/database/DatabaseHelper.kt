package com.solaro.app.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.solaro.app.models.Installation
import com.solaro.app.models.MaintenanceTicket
import com.solaro.app.models.User
import com.solaro.app.utils.PasswordHasher

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Solaro.db"

        // Users Table
        private const val TABLE_USERS = "users"
        private const val KEY_USER_ID_COL = "id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD_HASH = "password_hash"
        private const val KEY_USER_TYPE = "user_type"

        // Installations Table
        private const val TABLE_INSTALLATIONS = "installations"
        private const val KEY_INSTALL_ID = "id"
        private const val KEY_INSTALL_USER_ID = "user_id"
        private const val KEY_ADDRESS = "address"
        private const val KEY_STATUS = "status"

        // Maintenance Table
        private const val TABLE_MAINTENANCE = "maintenance"
        private const val KEY_MAINT_ID = "id"
        private const val KEY_MAINT_USER_ID = "user_id"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_IMAGE_PATH = "image_path"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT UNIQUE,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PASSWORD_HASH + " TEXT,"
                + KEY_USER_TYPE + " TEXT" + ")")
        db?.execSQL(createUsersTable)

        val createInstallationsTable = ("CREATE TABLE " + TABLE_INSTALLATIONS + "("
                + KEY_INSTALL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_INSTALL_USER_ID + " INTEGER,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_STATUS + " TEXT" + ")")
        db?.execSQL(createInstallationsTable)

        val createMaintenanceTable = ("CREATE TABLE " + TABLE_MAINTENANCE + "("
                + KEY_MAINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_MAINT_USER_ID + " INTEGER,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_IMAGE_PATH + " TEXT,"
                + KEY_STATUS + " TEXT" + ")")
        db?.execSQL(createMaintenanceTable)
        addDefaultAdmin(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INSTALLATIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MAINTENANCE")
        onCreate(db)
    }

    private fun addDefaultAdmin(db: SQLiteDatabase?) {
        val adminExists = db?.rawQuery("SELECT * FROM $TABLE_USERS WHERE $KEY_USER_TYPE = 'admin'", null)
        if (adminExists?.count == 0) {
            val adminUser = User(
                username = "admin",
                email = "admin@solaro.com",
                passwordHash = PasswordHasher.hashPassword("admin123"),
                userType = "admin"
            )
            val contentValues = ContentValues()
            contentValues.put(KEY_USERNAME, adminUser.username)
            contentValues.put(KEY_EMAIL, adminUser.email)
            contentValues.put(KEY_PASSWORD_HASH, adminUser.passwordHash)
            contentValues.put(KEY_USER_TYPE, adminUser.userType)
            db.insert(TABLE_USERS, null, contentValues)
        }
        adminExists?.close()
    }

    // ## User Functions ##
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

    @SuppressLint("Range")
    fun getUserByUsername(username: String): User? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $KEY_USERNAME = ?", arrayOf(username))
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getLong(cursor.getColumnIndex(KEY_USER_ID_COL)),
                username = cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL)),
                passwordHash = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD_HASH)),
                userType = cursor.getString(cursor.getColumnIndex(KEY_USER_TYPE))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    @SuppressLint("Range")
    fun getUserByEmail(email: String): User? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $KEY_EMAIL = ?", arrayOf(email))
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getLong(cursor.getColumnIndex(KEY_USER_ID_COL)),
                username = cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL)),
                passwordHash = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD_HASH)),
                userType = cursor.getString(cursor.getColumnIndex(KEY_USER_TYPE))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    @SuppressLint("Range")
    fun getAllUsers(): List<User> {
        val userList = ArrayList<User>()
        val selectQuery = "SELECT * FROM $TABLE_USERS WHERE $KEY_USER_TYPE != 'admin'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                userList.add(
                    User(
                        id = cursor.getLong(cursor.getColumnIndex(KEY_USER_ID_COL)),
                        username = cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                        email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL)),
                        passwordHash = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD_HASH)),
                        userType = cursor.getString(cursor.getColumnIndex(KEY_USER_TYPE))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    fun deleteUser(userId: Long): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_USERS, "$KEY_USER_ID_COL=?", arrayOf(userId.toString()))
        db.close()
        return success
    }

    // ## Installation Functions ##
    fun addInstallationRequest(userId: Long, address: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_INSTALL_USER_ID, userId)
        contentValues.put(KEY_ADDRESS, address)
        contentValues.put(KEY_STATUS, "Pending")
        val success = db.insert(TABLE_INSTALLATIONS, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllInstallations(): List<Installation> {
        val installationList = ArrayList<Installation>()
        val selectQuery = "SELECT * FROM $TABLE_INSTALLATIONS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                installationList.add(
                    Installation(
                        id = cursor.getLong(cursor.getColumnIndex(KEY_INSTALL_ID)),
                        userId = cursor.getLong(cursor.getColumnIndex(KEY_INSTALL_USER_ID)),
                        address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)),
                        status = cursor.getString(cursor.getColumnIndex(KEY_STATUS))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return installationList
    }

    fun updateInstallationStatus(installId: Long, newStatus: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_STATUS, newStatus)
        val success = db.update(TABLE_INSTALLATIONS, contentValues, "$KEY_INSTALL_ID=?", arrayOf(installId.toString()))
        db.close()
        return success
    }

    fun deleteInstallation(installId: Long): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_INSTALLATIONS, "$KEY_INSTALL_ID=?", arrayOf(installId.toString()))
        db.close()
        return success
    }

    // ## Maintenance Functions ##
    fun addMaintenanceTicket(userId: Long, description: String, imagePath: String?): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_MAINT_USER_ID, userId)
        contentValues.put(KEY_DESCRIPTION, description)
        contentValues.put(KEY_IMAGE_PATH, imagePath)
        contentValues.put(KEY_STATUS, "Open")
        val success = db.insert(TABLE_MAINTENANCE, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllMaintenanceTickets(): List<MaintenanceTicket> {
        val ticketList = ArrayList<MaintenanceTicket>()
        val selectQuery = "SELECT * FROM $TABLE_MAINTENANCE"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                ticketList.add(
                    MaintenanceTicket(
                        id = cursor.getLong(cursor.getColumnIndex(KEY_MAINT_ID)),
                        userId = cursor.getLong(cursor.getColumnIndex(KEY_MAINT_USER_ID)),
                        description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        imagePath = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)),
                        status = cursor.getString(cursor.getColumnIndex(KEY_STATUS))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return ticketList
    }

    fun updateMaintenanceStatus(ticketId: Long, newStatus: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_STATUS, newStatus)
        val success = db.update(TABLE_MAINTENANCE, contentValues, "$KEY_MAINT_ID=?", arrayOf(ticketId.toString()))
        db.close()
        return success
    }

    fun deleteMaintenanceTicket(ticketId: Long): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_MAINTENANCE, "$KEY_MAINT_ID=?", arrayOf(ticketId.toString()))
        db.close()
        return success
    }
}