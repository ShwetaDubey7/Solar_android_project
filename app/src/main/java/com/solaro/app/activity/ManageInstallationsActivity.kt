package com.solaro.app.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.solaro.app.adapter.InstallationAdapter
import com.solaro.app.database.DatabaseHelper
import com.solaro.databinding.ActivityManageInstallationsBinding

@Suppress("DEPRECATION")
class ManageInstallationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageInstallationsBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var installationAdapter: InstallationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageInstallationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadInstallations()
    }

    private fun setupRecyclerView() {
        installationAdapter = InstallationAdapter(mutableListOf(), dbHelper) {
            // Refresh the list after an item is deleted or updated
            loadInstallations()
        }
        binding.rvInstallations.apply {
            layoutManager = LinearLayoutManager(this@ManageInstallationsActivity)
            adapter = installationAdapter
        }
    }

    private fun loadInstallations() {
        val installationList = dbHelper.getAllInstallations()
        installationAdapter.updateInstallations(installationList)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}