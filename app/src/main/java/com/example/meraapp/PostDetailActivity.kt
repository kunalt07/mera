package com.example.meraapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meraapp.ComplaintRepository
import com.example.meraapp.Complaint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView

class PostDetailActivity : AppCompatActivity() {
    private var upvotes = 0
    private var downvotes = 0
    private val comments = mutableListOf<Pair<String, String>>() // (user, comment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide status bar and action bar for full screen
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(R.layout.activity_post_detail)

        val index = intent.getIntExtra("complaintIndex", -1)
        val complaint = if (index in ComplaintRepository.complaints.indices) ComplaintRepository.complaints[index] else null
        if (complaint == null) {
            finish()
            return
        }

        findViewById<TextView>(R.id.tvUserName).text = "Aneesh"
        findViewById<TextView>(R.id.tvUserLocation).text = "Dibdih"
        findViewById<TextView>(R.id.tvPostTitle).text = complaint.title
        findViewById<TextView>(R.id.tvPostDescription).text = complaint.description

        // Photos
        val rvPhotos = findViewById<RecyclerView>(R.id.rvPostPhotos)
        rvPhotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvPhotos.adapter = PostPhotoAdapter(complaint.photoUri?.let { listOf(it) } ?: emptyList())

        // Upvote/Downvote
        val tvUpvote = findViewById<TextView>(R.id.tvUpvoteCount)
        val tvDownvote = findViewById<TextView>(R.id.tvDownvoteCount)
        val ivUpvote = findViewById<ImageView>(R.id.ivUpvote)
        val ivDownvote = findViewById<ImageView>(R.id.ivDownvote)
        tvUpvote.text = upvotes.toString()
        tvDownvote.text = downvotes.toString()
        ivUpvote.setOnClickListener {
            upvotes++
            tvUpvote.text = upvotes.toString()
        }
        ivDownvote.setOnClickListener {
            downvotes++
            tvDownvote.text = downvotes.toString()
        }

        // Comments
        val rvComments = findViewById<RecyclerView>(R.id.rvComments)
        rvComments.layoutManager = LinearLayoutManager(this)
        val commentAdapter = CommentAdapter(comments)
        rvComments.adapter = commentAdapter

        val etAddComment = findViewById<EditText>(R.id.etAddComment)
        val btnPostComment = findViewById<Button>(R.id.btnPostComment)
        btnPostComment.setOnClickListener {
            val commentText = etAddComment.text.toString().trim()
            if (commentText.isNotEmpty()) {
                comments.add(Pair("User", commentText))
                commentAdapter.notifyItemInserted(comments.size - 1)
                etAddComment.text.clear()
            }
        }
    }
}

class PostPhotoAdapter(private val photos: List<Uri>) : RecyclerView.Adapter<PostPhotoAdapter.PhotoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post_photo, parent, false)
        return PhotoViewHolder(view)
    }
    override fun getItemCount(): Int = photos.size
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }
    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(uri: Uri) {
            (itemView as ImageView).setImageURI(uri)
        }
    }
}

class CommentAdapter(private val comments: List<Pair<String, String>>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }
    override fun getItemCount() = comments.size
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(comment: Pair<String, String>) {
            itemView.findViewById<TextView>(R.id.tvCommentUser).text = comment.first
            itemView.findViewById<TextView>(R.id.tvCommentText).text = comment.second
        }
    }
} 