package com.solaro.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.solaro.R
import com.solaro.app.LoginActivity
import com.solaro.app.utils.SessionManager

class AdminDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        sessionManager = SessionManager(this)

        // Set up the toolbar at the top of the screen
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // This creates the hamburger icon and connects it to the drawer
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val tvWelcomeAdmin: TextView = findViewById(R.id.tvWelcomeAdmin)
        val username = intent.getStringExtra("USER_NAME")
        tvWelcomeAdmin.text = "Welcome, ${username ?: "Admin"}!"
    }

    // Handles clicks inside the navigation drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_manage_installations -> {
                startActivity(Intent(this, ManageInstallationsActivity::class.java))
            }
            R.id.nav_manage_maintenance -> {
                startActivity(Intent(this, ManageMaintenanceActivity::class.java))
            }
            R.id.nav_manage_users -> {
                startActivity(Intent(this, ManageUsersActivity::class.java))
            }
        }
        // Close the drawer after an item is tapped
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Handles the back button press to close the drawer if it's open
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
