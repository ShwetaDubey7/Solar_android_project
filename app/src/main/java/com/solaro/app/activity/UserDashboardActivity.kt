package com.solaro.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.solaro.R
import com.solaro.app.adapter.RequestHistoryAdapter
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.RequestHistoryItem
import com.solaro.app.utils.SessionManager
import com.solaro.databinding.ActivityUserDashboardBinding

@Suppress("DEPRECATION")
class UserDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var requestHistoryAdapter: RequestHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)
        dbHelper = DatabaseHelper(this)

        binding.cardShop.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        setupUI()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadRequestHistory()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        // Setup Toolbar and Drawer
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayoutUser, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayoutUser.addDrawerListener(toggle)
        toggle.syncState()
        binding.navViewUser.setNavigationItemSelectedListener(this)

        // Set welcome messages
        val username = intent.getStringExtra(LoginActivity.EXTRA_USER_NAME)
        binding.tvWelcomeUser.text = "Welcome, ${username ?: "User"}!"

        val headerView = binding.navViewUser.getHeaderView(0)
        val navUsername = headerView.findViewById<TextView>(R.id.tvNavHeaderUsername)
        navUsername.text = username ?: "Solaro User"

        // Set card click listeners
        binding.cardRequestInstallation.setOnClickListener {
            startActivity(Intent(this, RequestInstallationActivity::class.java))
        }
        binding.cardRequestMaintenance.setOnClickListener {
            startActivity(Intent(this, RequestMaintenanceActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        requestHistoryAdapter = RequestHistoryAdapter(mutableListOf())
        binding.rvRequestHistory.apply {
            layoutManager = LinearLayoutManager(this@UserDashboardActivity)
            adapter = requestHistoryAdapter
        }
    }

    private fun loadRequestHistory() {
        val userId = sessionManager.getUserId()
        if (userId == -1L) return // Not logged in

        val historyItems = mutableListOf<RequestHistoryItem>()

        // Get installations and map them to history items
        val installations = dbHelper.getInstallationsForUser(userId)
        installations.forEach {
            historyItems.add(RequestHistoryItem(it.id, "Installation", it.address, it.status))
        }

        // Get maintenance tickets and map them to history items
        val tickets = dbHelper.getMaintenanceTicketsForUser(userId)
        tickets.forEach {
            historyItems.add(RequestHistoryItem(it.id, "Maintenance", it.description, it.status))
        }

        // Sort by ID descending to show newest first
        historyItems.sortByDescending { it.id }

        if (historyItems.isEmpty()) {
            binding.rvRequestHistory.visibility = View.GONE
            binding.tvNoRequests.visibility = View.VISIBLE
        } else {
            binding.rvRequestHistory.visibility = View.VISIBLE
            binding.tvNoRequests.visibility = View.GONE
            requestHistoryAdapter.updateItems(historyItems)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.nav_user_logout -> logout()
        }
        binding.drawerLayoutUser.closeDrawer(GravityCompat.START)
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
        if (binding.drawerLayoutUser.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayoutUser.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}