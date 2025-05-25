package com.example.hw2.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hw2.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class HighScoresMapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_high_scores_map, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.high_scores_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        return view
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }


    fun zoomToLocation(lat: Double, lon: Double) {
        googleMap?.clear()
        val position = LatLng(lat, lon)
        googleMap?.addMarker(MarkerOptions().position(position).title("High Score Location"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
    }
}
