package com.solaro.app.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.solaro.R
import com.solaro.app.adapter.ProductAdminAdapter
import com.solaro.app.database.DatabaseHelper
import com.solaro.app.models.Product
import com.solaro.databinding.ActivityManageProductsBinding

@Suppress("DEPRECATION")
class ManageProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageProductsBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var productAdminAdapter: ProductAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        setupRecyclerView()

        binding.fabAddProduct.setOnClickListener {
            showAddEditProductDialog(null)
        }
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun setupRecyclerView() {
        productAdminAdapter = ProductAdminAdapter(
            mutableListOf(),
            onEditClick = { product -> showAddEditProductDialog(product) },
            onDeleteClick = { product -> showDeleteConfirmationDialog(product) }
        )
        binding.rvProductsAdmin.apply {
            layoutManager = LinearLayoutManager(this@ManageProductsActivity)
            adapter = productAdminAdapter
        }
    }

    private fun loadProducts() {
        val productList = dbHelper.getAllProducts()
        productAdminAdapter.updateProducts(productList)
    }

    private fun showAddEditProductDialog(product: Product?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_product, null)
        val etName = dialogView.findViewById<EditText>(R.id.etProductName)
        val etDesc = dialogView.findViewById<EditText>(R.id.etProductDesc)
        val etPrice = dialogView.findViewById<EditText>(R.id.etProductPrice)
        val etImageUrl = dialogView.findViewById<EditText>(R.id.etProductImageUrl)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)

        val dialogTitle = if (product == null) "Add New Product" else "Edit Product"
        tvTitle.text = dialogTitle

        product?.let {
            etName.setText(it.name)
            etDesc.setText(it.description)
            etPrice.setText(it.price.toString())
            etImageUrl.setText(it.imageUrl)
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()
            .apply {
                setOnShowListener {
                    val saveButton = getButton(AlertDialog.BUTTON_POSITIVE)
                    saveButton.setOnClickListener {
                        val name = etName.text.toString().trim()
                        val desc = etDesc.text.toString().trim()
                        val price = etPrice.text.toString().toDoubleOrNull()
                        val imageUrl = etImageUrl.text.toString().trim()

                        if (name.isEmpty() || desc.isEmpty() || price == null || imageUrl.isEmpty()) {
                            Toast.makeText(this@ManageProductsActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        val newProduct = Product(
                            id = product?.id ?: 0,
                            name = name,
                            description = desc,
                            price = price,
                            imageUrl = imageUrl
                        )

                        if (product == null) {
                            dbHelper.addProduct(newProduct)
                        } else {
                            dbHelper.updateProduct(newProduct)
                        }
                        loadProducts()
                        dismiss()
                    }
                }
            }.show()
    }

    private fun showDeleteConfirmationDialog(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete '${product.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                dbHelper.deleteProduct(product.id)
                loadProducts()
                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}