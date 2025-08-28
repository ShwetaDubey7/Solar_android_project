package com.solaro.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // New import
import com.solaro.R
import com.solaro.app.activity.PaymentActivity
import com.solaro.app.models.Product
import com.solaro.databinding.ListItemProductUserBinding

class ProductUserAdapter(
    private val productList: List<Product>,
    private val context: Context
) : RecyclerView.Adapter<ProductUserAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ListItemProductUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ListItemProductUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.tvProductName.text = product.name
        holder.binding.tvProductDescription.text = product.description
        holder.binding.tvProductPrice.text = "₹ ${"%,.2f".format(product.price)}"

        // Use Glide to load the image from the URL
        Glide.with(context)
            .load(product.imageUrl)
            .placeholder(R.drawable.img_solar_header) // Optional: show a placeholder while loading
            .into(holder.binding.ivProductImage)

        holder.binding.btnBuyNow.setOnClickListener {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra("PRODUCT_EXTRA", product)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = productList.size
}