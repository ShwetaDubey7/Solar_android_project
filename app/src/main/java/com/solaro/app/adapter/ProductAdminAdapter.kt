package com.solaro.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.solaro.app.models.Product
import com.solaro.databinding.ListItemProductAdminBinding

class ProductAdminAdapter(
    private var productList: MutableList<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdminAdapter.ProductAdminViewHolder>() {

    inner class ProductAdminViewHolder(val binding: ListItemProductAdminBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdminViewHolder {
        val binding = ListItemProductAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductAdminViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductAdminViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.tvProductName.text = product.name
        holder.binding.tvProductPrice.text = "₹ ${"%,.2f".format(product.price)}"

        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .into(holder.binding.ivProductImage)

        holder.binding.btnEditProduct.setOnClickListener { onEditClick(product) }
        holder.binding.btnDeleteProduct.setOnClickListener { onDeleteClick(product) }
    }

    override fun getItemCount(): Int = productList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(newList: List<Product>) {
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }
}