// In RequestInstallationActivity.kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.solaro.R
import com.app.solaro.database.DatabaseHelper
import com.solaro.utils.SessionManager

class RequestInstallationActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_installation)

        dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        val etAddress = findViewById<EditText>(R.id.etInstallationAddress)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitRequest)

        btnSubmit.setOnClickListener {
            val address = etAddress.text.toString().trim()
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get the logged-in user's ID from the session
            val userId = sessionManager.getUserId()
            if (userId != -1L) {
                val result = dbHelper.addInstallationRequest(userId, address)
                if (result != -1L) {
                    Toast.makeText(this, "Request submitted successfully!", Toast.LENGTH_LONG).show()
                    finish() // Go back to the dashboard
                } else {
                    Toast.makeText(this, "Failed to submit request.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}