package com.example.meraapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.AppCompatButton

class SelectCategoryActivity : AppCompatActivity() {
    private val selectedIndices = mutableSetOf<Int>()
    private val categoryCount = 28 // 4x7 grid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide status bar and action bar for full screen
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(R.layout.activity_select_category)

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategoriesGrid)
        val btnSelect = findViewById<AppCompatButton>(R.id.btnSelectCategory)

        val adapter = CategoryOptionAdapter(categoryCount, selectedIndices) { index, isSelected ->
            if (isSelected) selectedIndices.add(index) else selectedIndices.remove(index)
        }
        rvCategories.layoutManager = GridLayoutManager(this, 4)
        rvCategories.adapter = adapter

        btnSelect.setOnClickListener {
            if (selectedIndices.isNotEmpty()) {
                val data = Intent().apply { putExtra("selectedCategories", selectedIndices.toIntArray()) }
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }
}

class CategoryOptionAdapter(private val count: Int, private val selectedIndices: Set<Int>, private val onSelect: (Int, Boolean) -> Unit) : RecyclerView.Adapter<CategoryOptionAdapter.CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_option, parent, false)
        return CategoryViewHolder(view)
    }
    override fun getItemCount() = count
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(selectedIndices.contains(position))
        holder.itemView.setOnClickListener {
            val isSelected = !selectedIndices.contains(position)
            onSelect(position, isSelected)
            notifyItemChanged(position)
        }
    }
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCheckmark: ImageView = itemView.findViewById(R.id.ivCheckmark)
        private val overlayGrey: View = itemView.findViewById(R.id.overlayGrey)
        fun bind(selected: Boolean) {
            ivCheckmark.visibility = if (selected) View.VISIBLE else View.GONE
            overlayGrey.visibility = if (selected) View.VISIBLE else View.GONE
        }
    }
} 