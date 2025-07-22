package com.example.meraapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import android.widget.ImageView
import android.view.View
import com.example.meraapp.Complaint
import com.example.meraapp.ComplaintRepository

class FileComplaintActivity : AppCompatActivity() {
    private var selectedImageUri: Uri? = null
    private lateinit var ivSelectedPhoto: ImageView
    private lateinit var btnCategory: AppCompatButton
    private var selectedCategories: List<Int> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide status bar and action bar for full screen
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(R.layout.activity_file_complaint)

        val btnAddPhoto = findViewById<AppCompatButton>(R.id.btnAddPhoto)
        ivSelectedPhoto = findViewById(R.id.ivSelectedPhoto)
        btnAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 101)
        }
        btnCategory = findViewById(R.id.btnCategory)
        btnCategory.setOnClickListener {
            val intent = Intent(this, SelectCategoryActivity::class.java)
            startActivityForResult(intent, 201)
        }
        val btnPost = findViewById<AppCompatButton>(R.id.btnPost)
        btnPost.setOnClickListener {
            val title = findViewById<android.widget.EditText>(R.id.etTitle).text.toString()
            val description = findViewById<android.widget.EditText>(R.id.etDescription).text.toString()
            if (title.isNotBlank() && description.isNotBlank() && selectedCategories.isNotEmpty()) {
                ComplaintRepository.complaints.add(
                    Complaint(title, description, selectedCategories, selectedImageUri)
                )
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields and select at least one category", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            ivSelectedPhoto.setImageURI(selectedImageUri)
            ivSelectedPhoto.visibility = View.VISIBLE
            Toast.makeText(this, "Photo selected!", Toast.LENGTH_SHORT).show()
            // You can display the image or store the URI as needed
        }
        if (requestCode == 201 && resultCode == Activity.RESULT_OK && data != null) {
            val selected = data.getIntArrayExtra("selectedCategories")
            if (selected != null && selected.isNotEmpty()) {
                selectedCategories = selected.toList()
                btnCategory.text = "${selected.size} selected"
            }
        }
    }
} 