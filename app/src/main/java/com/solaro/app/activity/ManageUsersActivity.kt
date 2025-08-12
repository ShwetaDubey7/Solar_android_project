package com.solaro.app.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.solaro.R
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.User

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var usersAdapter: UserAdapter
    private var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_users)

        dbHelper = DatabaseHelper(this)
        val rvUsers = findViewById<RecyclerView>(R.id.rvUsers)
        rvUsers.layoutManager = LinearLayoutManager(this)

        // The adapter is passed a lambda function to refresh the list
        usersAdapter = UserAdapter(userList) {
            loadUsers()
        }
        rvUsers.adapter = usersAdapter
    }

    override fun onResume() {
        super.onResume()
        // Refresh the user list every time the screen is shown
        loadUsers()
    }

    private fun loadUsers() {
        val allUsers = dbHelper.getAllUsers()
        userList.clear()
        userList.addAll(allUsers)
        usersAdapter.notifyDataSetChanged()
    }
}

/**
 * Adapter to connect the User data to the RecyclerView.
 */
class UserAdapter(
    private val users: List<User>,
    private val refreshData: () -> Unit // Function to refresh the list
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // Holds the views for a single user item.
    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.tvUsername)
        val email: TextView = view.findViewById(R.id.tvUserEmail)
        val deleteButton: Button = view.findViewById(R.id.btnDeleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val context = holder.itemView.context
        val dbHelper = DatabaseHelper(context)

        holder.username.text = user.username
        holder.email.text = user.email

        // Set listener for the delete button
        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete ${user.username}?")
                .setPositiveButton("Yes") { _, _ ->
                    val success = dbHelper.deleteUser(user.id)
                    if (success > 0) {
                        Toast.makeText(context, "User deleted.", Toast.LENGTH_SHORT).show()
                        refreshData() // Refresh the list
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount() = users.size
}
