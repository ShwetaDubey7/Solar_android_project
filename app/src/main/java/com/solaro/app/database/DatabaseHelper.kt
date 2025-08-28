package com.solaro.app.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.solaro.app.models.Installation
import com.solaro.app.models.MaintenanceTicket
import com.solaro.app.models.Product
import com.solaro.app.models.User
import com.solaro.app.utils.PasswordHasher

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "Solaro.db"

        // Products Table (NEW)
        private const val TABLE_PRODUCTS = "products"
        private const val KEY_PRODUCT_ID = "id"
        private const val KEY_PRODUCT_NAME = "name"
        private const val KEY_PRODUCT_DESC = "description"
        private const val KEY_PRODUCT_PRICE = "price"
        private const val KEY_PRODUCT_IMAGE_URL = "image_url"

        // Orders Table (NEW)
        private const val TABLE_ORDERS = "orders"
        private const val KEY_ORDER_ID = "id"
        private const val KEY_ORDER_USER_ID = "user_id"
        private const val KEY_ORDER_PRODUCT_ID = "product_id"
        private const val KEY_ORDER_DATE = "order_date"

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
        private const val KEY_STATUS = "status" // e.g., "Pending", "In Progress", "Completed", "Cancelled"

        // Maintenance Table
        private const val TABLE_MAINTENANCE = "maintenance"
        private const val KEY_MAINT_ID = "id"
        private const val KEY_MAINT_USER_ID = "user_id"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_IMAGE_PATH = "image_path" // Path to the image file
        private const val KEY_MAINT_STATUS = "status" // e.g., "Open", "Assigned", "Resolved", "Closed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT UNIQUE,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PASSWORD_HASH + " TEXT,"
                + KEY_USER_TYPE + " TEXT NOT NULL" + ")")
        db?.execSQL(createUsersTable)

        val createInstallationsTable = ("CREATE TABLE " + TABLE_INSTALLATIONS + "("
                + KEY_INSTALL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_INSTALL_USER_ID + " INTEGER NOT NULL,"
                + KEY_ADDRESS + " TEXT NOT NULL,"
                + KEY_STATUS + " TEXT NOT NULL" + ")")
        db?.execSQL(createInstallationsTable)

        val createMaintenanceTable = ("CREATE TABLE " + TABLE_MAINTENANCE + "("
                + KEY_MAINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_MAINT_USER_ID + " INTEGER NOT NULL,"
                + KEY_DESCRIPTION + " TEXT NOT NULL,"
                + KEY_IMAGE_PATH + " TEXT," // Image path is optional
                + KEY_MAINT_STATUS + " TEXT NOT NULL" + ")")
        db?.execSQL(createMaintenanceTable)

        val createProductsTable = ("CREATE TABLE " + TABLE_PRODUCTS + "("
                + KEY_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PRODUCT_NAME + " TEXT,"
                + KEY_PRODUCT_DESC + " TEXT,"
                + KEY_PRODUCT_PRICE + " REAL,"
                + KEY_PRODUCT_IMAGE_URL + " TEXT" + ")")
        db?.execSQL(createProductsTable)

        val createOrdersTable = ("CREATE TABLE " + TABLE_ORDERS + "("
                + KEY_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ORDER_USER_ID + " INTEGER,"
                + KEY_ORDER_PRODUCT_ID + " INTEGER,"
                + KEY_ORDER_DATE + " TEXT" + ")")
        db?.execSQL(createOrdersTable)

        addDefaultAdmin(db)
        addDefaultProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        if (oldVersion < 2) {
            val createProductsTable = ("CREATE TABLE " + TABLE_PRODUCTS + "("
                    + KEY_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_PRODUCT_NAME + " TEXT,"
                    + KEY_PRODUCT_DESC + " TEXT,"
                    + KEY_PRODUCT_PRICE + " REAL,"
                    + KEY_PRODUCT_IMAGE_URL + " TEXT" + ")")
            db?.execSQL(createProductsTable)

            val createOrdersTable = ("CREATE TABLE " + TABLE_ORDERS + "("
                    + KEY_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_ORDER_USER_ID + " INTEGER,"
                    + KEY_ORDER_PRODUCT_ID + " INTEGER,"
                    + KEY_ORDER_DATE + " TEXT" + ")")
            db?.execSQL(createOrdersTable)
            addDefaultProducts(db)
        }

        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INSTALLATIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MAINTENANCE")
        // Create tables again
        onCreate(db)
    }

    private fun addDefaultProducts(db: SQLiteDatabase?) {
        val products = listOf(
            Product(name = "Luminous Solar Panel", description = "395 Watt, 24 Volt Mono PERC Solar Panel for home.", price = 13500.0, imageUrl = "https://placehold.co/600x400/FFC107/FFFFFF?text=Panel"),
            Product(name = "Luminous Solar Inverter", description = "Solar Hybrid 1100/12V, Home UPS.", price = 7800.0, imageUrl = "https://placehold.co/600x400/03A9F4/FFFFFF?text=Inverter"),
            Product(name = "Solar Battery", description = "Luminous 150Ah Solar Tall Tubular Battery.", price = 15000.0, imageUrl = "https://placehold.co/600x400/4CAF50/FFFFFF?text=Battery")
        )
        products.forEach { product ->
            val contentValues = ContentValues().apply {
                put(KEY_PRODUCT_NAME, product.name)
                put(KEY_PRODUCT_DESC, product.description)
                put(KEY_PRODUCT_PRICE, product.price)
                put(KEY_PRODUCT_IMAGE_URL, product.imageUrl)
            }
            db?.insert(TABLE_PRODUCTS, null, contentValues)
        }
    }

    // Call this to add a default admin user if one doesn't exist
    private fun addDefaultAdmin(db: SQLiteDatabase?) {
        // Check if an admin user already exists
        val cursor = db?.rawQuery("SELECT * FROM $TABLE_USERS WHERE $KEY_USER_TYPE = 'admin'", null)
        if (cursor != null && cursor.count == 0) {
            val adminUser = User(
                username = "admin",
                email = "admin@solaro.com",
                passwordHash = PasswordHasher.hashPassword("admin123"), // Simple password for demo
                userType = "admin"
            )
            val contentValues = ContentValues().apply {
                put(KEY_USERNAME, adminUser.username)
                put(KEY_EMAIL, adminUser.email)
                put(KEY_PASSWORD_HASH, adminUser.passwordHash)
                put(KEY_USER_TYPE, adminUser.userType)
            }
            db.insert(TABLE_USERS, null, contentValues)
        }
        cursor?.close()
    }

    // ## User Functions ##

    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_USERNAME, user.username)
            put(KEY_EMAIL, user.email)
            put(KEY_PASSWORD_HASH, user.passwordHash)
            put(KEY_USER_TYPE, user.userType)
        }
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
                // Ensure userType is always lowercase for consistent comparison in app logic
                userType = cursor.getString(cursor.getColumnIndex(KEY_USER_TYPE)).lowercase()
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
                userType = cursor.getString(cursor.getColumnIndex(KEY_USER_TYPE)).lowercase()
            )
        }
        cursor.close()
        db.close()
        return user
    }

    @SuppressLint("Range")
    fun getAllUsers(): List<User> {
        val userList = ArrayList<User>()
        // Exclude the 'admin' user from the list shown to admins
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
                        userType = cursor.getString(cursor.getColumnIndex(KEY_USER_TYPE)).lowercase()
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
        val contentValues = ContentValues().apply {
            put(KEY_INSTALL_USER_ID, userId)
            put(KEY_ADDRESS, address)
            put(KEY_STATUS, "Pending") // Default status
        }
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
        val contentValues = ContentValues().apply {
            put(KEY_STATUS, newStatus)
        }
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
        val contentValues = ContentValues().apply {
            put(KEY_MAINT_USER_ID, userId)
            put(KEY_DESCRIPTION, description)
            put(KEY_IMAGE_PATH, imagePath)
            put(KEY_MAINT_STATUS, "Open") // Default status
        }
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
                        status = cursor.getString(cursor.getColumnIndex(KEY_MAINT_STATUS))
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
        val contentValues = ContentValues().apply {
            put(KEY_MAINT_STATUS, newStatus)
        }
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

    @SuppressLint("Range")
    fun getInstallationsForUser(userId: Long): List<Installation> {
        val installationList = ArrayList<Installation>()
        val selectQuery = "SELECT * FROM $TABLE_INSTALLATIONS WHERE $KEY_INSTALL_USER_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))

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
        // db.close() // Do not close DB here if you call another method right after
        return installationList
    }

    //maintenance ticket for user
    @SuppressLint("Range")
    fun getMaintenanceTicketsForUser(userId: Long): List<MaintenanceTicket> {
        val ticketList = ArrayList<MaintenanceTicket>()
        val selectQuery = "SELECT * FROM $TABLE_MAINTENANCE WHERE $KEY_MAINT_USER_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                ticketList.add(
                    MaintenanceTicket(
                        id = cursor.getLong(cursor.getColumnIndex(KEY_MAINT_ID)),
                        userId = cursor.getLong(cursor.getColumnIndex(KEY_MAINT_USER_ID)),
                        description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        imagePath = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)),
                        status = cursor.getString(cursor.getColumnIndex(KEY_MAINT_STATUS))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        // db.close()
        return ticketList
    }


    fun addProduct(product: Product): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_PRODUCT_NAME, product.name)
            put(KEY_PRODUCT_DESC, product.description)
            put(KEY_PRODUCT_PRICE, product.price)
            put(KEY_PRODUCT_IMAGE_URL, product.imageUrl)
        }
        val success = db.insert(TABLE_PRODUCTS, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllProducts(): List<Product> {
        val productList = ArrayList<Product>()
        val selectQuery = "SELECT * FROM $TABLE_PRODUCTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                productList.add(
                    Product(
                        id = cursor.getLong(cursor.getColumnIndex(KEY_PRODUCT_ID)),
                        name = cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_NAME)),
                        description = cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_DESC)),
                        price = cursor.getDouble(cursor.getColumnIndex(KEY_PRODUCT_PRICE)),
                        imageUrl = cursor.getString(cursor.getColumnIndex(KEY_PRODUCT_IMAGE_URL))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    // Order Functions
    fun addOrder(userId: Long, productId: Long): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_ORDER_USER_ID, userId)
            put(KEY_ORDER_PRODUCT_ID, productId)
            put(KEY_ORDER_DATE, System.currentTimeMillis().toString())
        }
        val success = db.insert(TABLE_ORDERS, null, contentValues)
        db.close()
        return success
    }

    fun updateProduct(product: Product): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_PRODUCT_NAME, product.name)
            put(KEY_PRODUCT_DESC, product.description)
            put(KEY_PRODUCT_PRICE, product.price)
            put(KEY_PRODUCT_IMAGE_URL, product.imageUrl)
        }
        val success = db.update(TABLE_PRODUCTS, contentValues, "$KEY_PRODUCT_ID=?", arrayOf(product.id.toString()))
        db.close()
        return success
    }

    fun deleteProduct(productId: Long): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_PRODUCTS, "$KEY_PRODUCT_ID=?", arrayOf(productId.toString()))
        db.close()
        return success
    }
}