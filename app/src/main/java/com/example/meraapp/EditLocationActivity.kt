package com.example.meraapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.Button

class EditLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private var selectedLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_location)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val btnConfirm = findViewById<Button>(R.id.btnConfirmLocation)
        btnConfirm.setOnClickListener {
            selectedLatLng?.let {
                val data = Intent().apply {
                    putExtra("lat", it.latitude)
                    putExtra("lng", it.longitude)
                }
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val initialLatLng = LatLng(23.3441, 85.3096) // Ranchi default
        marker = map.addMarker(MarkerOptions().position(initialLatLng).draggable(true))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 15f))
        selectedLatLng = initialLatLng

        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(p0: Marker) {}
            override fun onMarkerDrag(p0: Marker) {}
            override fun onMarkerDragEnd(p0: Marker) {
                selectedLatLng = p0.position
            }
        })
        map.setOnMapClickListener { latLng ->
            marker?.position = latLng
            selectedLatLng = latLng
        }
    }
} 