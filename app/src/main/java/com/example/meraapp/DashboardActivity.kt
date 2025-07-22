package com.example.meraapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.widget.TextView
import com.example.meraapp.ComplaintRepository
import com.example.meraapp.Complaint
import android.net.Uri
import android.widget.ImageView

class DashboardActivity : AppCompatActivity() {
    private lateinit var feedAdapter: ComplaintFeedAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide status bar and action bar for full screen
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(R.layout.activity_dashboard)

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        val rvFeed = findViewById<RecyclerView>(R.id.rvFeed)

        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = PlaceholderAdapter(R.layout.item_dashboard_category, 6)

        feedAdapter = ComplaintFeedAdapter { index ->
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("complaintIndex", index)
            startActivity(intent)
        }
        rvFeed.layoutManager = LinearLayoutManager(this)
        rvFeed.adapter = feedAdapter

        val tvLocationTitle = findViewById<TextView>(R.id.tvLocationTitle)
        val tvLocationSubtitle = findViewById<TextView>(R.id.tvLocationSubtitle)
        val locationHeaderClickListener = View.OnClickListener {
            val intent = Intent(this, EditLocationActivity::class.java)
            startActivityForResult(intent, 1001)
        }
        tvLocationTitle.setOnClickListener(locationHeaderClickListener)
        tvLocationSubtitle.setOnClickListener(locationHeaderClickListener)

        val btnAddNew = findViewById<View>(R.id.btnAddNew)
        btnAddNew.setOnClickListener {
            val intent = Intent(this, FileComplaintActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        feedAdapter.updateData(ComplaintRepository.complaints)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            val lat = data.getDoubleExtra("lat", 0.0)
            val lng = data.getDoubleExtra("lng", 0.0)
            // You can use a geocoder here to get the address, for now just show coordinates
            val tvLocationTitle = findViewById<TextView>(R.id.tvLocationTitle)
            val tvLocationSubtitle = findViewById<TextView>(R.id.tvLocationSubtitle)
            tvLocationTitle.text = "Lat: %.4f".format(lat)
            tvLocationSubtitle.text = "Lng: %.4f".format(lng)
        }
    }
}

class PlaceholderAdapter(private val layoutId: Int, private val count: Int) : RecyclerView.Adapter<PlaceholderAdapter.PlaceholderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceholderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return PlaceholderViewHolder(view)
    }
    override fun getItemCount() = count
    override fun onBindViewHolder(holder: PlaceholderViewHolder, position: Int) {}
    class PlaceholderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

class ComplaintFeedAdapter(private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<ComplaintFeedAdapter.FeedViewHolder>() {
    private var complaints: List<Complaint> = emptyList()
    fun updateData(newData: List<Complaint>) {
        complaints = newData.reversed() // Show newest first
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dashboard_feed, parent, false)
        return FeedViewHolder(view)
    }
    override fun getItemCount() = complaints.size
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(complaints[position])
        holder.itemView.setOnClickListener { onItemClick(complaints.size - 1 - position) }
    }
    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(complaint: Complaint) {
            val tvTitle = itemView.findViewById<TextView>(R.id.tvFeedTitle)
            val tvDesc = itemView.findViewById<TextView>(R.id.tvFeedDesc)
            val tvCat = itemView.findViewById<TextView>(R.id.tvFeedCategories)
            val ivPhoto = itemView.findViewById<ImageView?>(R.id.ivFeedPhoto)
            tvTitle?.text = complaint.title
            tvDesc?.text = complaint.description
            tvCat?.text = "Categories: ${complaint.categories.size}"
            if (ivPhoto != null && complaint.photoUri != null) {
                ivPhoto.setImageURI(complaint.photoUri)
                ivPhoto.visibility = View.VISIBLE
            } else if (ivPhoto != null) {
                ivPhoto.visibility = View.GONE
            }
        }
    }
} 