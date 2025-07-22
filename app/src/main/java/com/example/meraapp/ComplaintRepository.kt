package com.example.meraapp

import android.net.Uri

data class Complaint(
    val title: String,
    val description: String,
    val categories: List<Int>,
    val photoUri: Uri?
)

object ComplaintRepository {
    val complaints = mutableListOf<Complaint>()
} 