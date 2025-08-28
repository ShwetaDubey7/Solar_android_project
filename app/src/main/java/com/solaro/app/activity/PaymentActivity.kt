package com.solaro.app.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.Product
import com.solaro.app.utils.SessionManager
import com.solaro.databinding.ActivityPaymentBinding

@Suppress("DEPRECATION")
class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val product = intent.getSerializableExtra("PRODUCT_EXTRA") as? Product

        if (product == null) {
            Toast.makeText(this, "Error: Product not found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvProductName.text = product.name
        binding.tvProductPrice.text = "Amount: ₹ ${"%,.2f".format(product.price)}"

        binding.btnPayNow.setOnClickListener {
            // This is a dummy payment process
            val userId = sessionManager.getUserId()
            if (userId != -1L) {
                dbHelper.addOrder(userId, product.id)
                Toast.makeText(this, "Payment Successful! Order placed.", Toast.LENGTH_LONG).show()
                finish() // Close payment activity
            } else {
                Toast.makeText(this, "Error: You are not logged in.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}