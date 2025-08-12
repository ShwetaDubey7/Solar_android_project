package com.solaro.app.activity

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.solaro.R
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.utils.SessionManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RequestMaintenanceActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    private lateinit var etDescription: EditText
    private lateinit var ivPhotoPreview: ImageView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnSubmit: Button

    private var latestTmpUri: Uri? = null
    private var photoPath: String? = null

    // Modern way to handle the result from the camera activity
    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                ivPhotoPreview.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_maintenance)

        dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        etDescription = findViewById(R.id.etMaintDescription)
        ivPhotoPreview = findViewById(R.id.ivPhotoPreview)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnSubmit = findViewById(R.id.btnSubmitTicket)

        btnTakePhoto.setOnClickListener {
            takeImage()
        }

        btnSubmit.setOnClickListener {
            submitTicket()
        }
    }

    private fun takeImage() {
        getTmpFileUri().let { uri ->
            latestTmpUri = uri
            takeImageResult.launch(uri)
        }
    }

    private fun getTmpFileUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val tmpFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            .apply {
                // Save the absolute path to be stored in the database
                photoPath = absolutePath
            }

        // Use FileProvider to create a secure URI for the camera app
        return FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", tmpFile)
    }

    private fun submitTicket() {
        val description = etDescription.text.toString().trim()
        if (description.isEmpty()) {
            Toast.makeText(this, "Please describe the issue.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = sessionManager.getUserId()
        if (userId == -1L) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Add the ticket to the database, including the optional photo path
        val result = dbHelper.addMaintenanceTicket(userId, description, photoPath)
        if (result != -1L) {
            Toast.makeText(this, "Maintenance ticket submitted successfully!", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to submit ticket.", Toast.LENGTH_SHORT).show()
        }
    }
}
