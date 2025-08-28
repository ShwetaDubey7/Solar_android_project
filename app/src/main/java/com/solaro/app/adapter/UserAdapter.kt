package com.solaro.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.User
import com.solaro.databinding.ListItemUserBinding

class UserAdapter(
    private var userList: MutableList<User>,
    private val dbHelper: DatabaseHelper,
    private val onUserDeleted: () -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ListItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ListItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.tvUsername.text = user.username
        holder.binding.tvUserEmail.text = user.email

        holder.binding.btnDeleteUser.setOnClickListener {
            val context = holder.itemView.context
            AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete ${user.username}?")
                .setPositiveButton("Delete") { _, _ ->
                    val success = dbHelper.deleteUser(user.id)
                    if (success > 0) {
                        Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                        onUserDeleted() // Callback to refresh the list in the activity
                    } else {
                        Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int = userList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsers(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }
}
