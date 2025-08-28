package com.solaro.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.solaro.R
import com.solaro.app.utils.SessionManager
import com.solaro.databinding.ActivityAdminDashboardBinding

@Suppress("DEPRECATION")
class AdminDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var sessionManager: SessionManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        // Setup Toolbar and Drawer
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        // Set welcome message
        val username = intent.getStringExtra(LoginActivity.EXTRA_USER_NAME)
        binding.tvWelcomeAdmin.text = "Welcome, ${username ?: "Admin"}!"

        // Set click listeners for the new cards in the dashboard
        setupCardClickListeners()
    }

    private fun setupCardClickListeners() {
        binding.cardManageUsers.setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java))
        }
        binding.cardManageInstallations.setOnClickListener {
            startActivity(Intent(this, ManageInstallationsActivity::class.java))
        }
        binding.cardManageMaintenance.setOnClickListener {
            startActivity(Intent(this, ManageMaintenanceActivity::class.java))
        }
        binding.cardManageProducts.setOnClickListener {
            startActivity(Intent(this, ManageProductsActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_manage_users -> startActivity(Intent(this, ManageUsersActivity::class.java))
            R.id.nav_manage_installations -> startActivity(Intent(this, ManageInstallationsActivity::class.java))
            R.id.nav_manage_maintenance -> startActivity(Intent(this, ManageMaintenanceActivity::class.java))
            R.id.nav_manage_products -> startActivity(Intent(this, ManageProductsActivity::class.java))
            R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.nav_admin_logout -> logout()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        sessionManager.logoutUser()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}