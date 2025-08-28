package com.solaro.app.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.solaro.app.adapter.UserAdapter
import com.solaro.app.database.DatabaseHelper
import com.solaro.databinding.ActivityManageUsersBinding

@Suppress("DEPRECATION")
class ManageUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageUsersBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(mutableListOf(), dbHelper) {
            // This lambda is called when a user is deleted in the adapter
            // to refresh the list in the activity.
            loadUsers()
        }
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@ManageUsersActivity)
            adapter = userAdapter
        }
    }

    private fun loadUsers() {
        val userList = dbHelper.getAllUsers()
        userAdapter.updateUsers(userList)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
