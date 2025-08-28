package com.solaro.app.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.solaro.app.adapter.ProductUserAdapter
import com.solaro.app.database.DatabaseHelper
import com.solaro.databinding.ActivityShopBinding

@Suppress("DEPRECATION")
class ShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var productAdapter: ProductUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductUserAdapter(dbHelper.getAllProducts(), this)
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(this@ShopActivity)
            adapter = productAdapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}