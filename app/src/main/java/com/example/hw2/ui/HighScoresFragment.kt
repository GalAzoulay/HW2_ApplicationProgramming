package com.example.hw2.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hw2.R
import com.example.hw2.interfaces.Callback_HighScoreItemClicked


class HighScoresFragment : Fragment() {

    private lateinit var highScores_LIST: ListView

    private var scoreSelectedCallback: Callback_HighScoreItemClicked? = null

    private lateinit var displayedDistances: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_high_scores, container, false)
        findViews(view)
        displayedDistances = loadDistances()
        initViews()
        return view
    }


    private fun findViews(v: View) {
        highScores_LIST = v.findViewById(R.id.high_scores_LIST)
    }


    private fun initViews() {
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, displayedDistances)
        highScores_LIST.adapter = adapter

        highScores_LIST.setOnItemClickListener { _, _, position, _ ->
            val location = getLocationForPosition(position)
            if (location != null) {
                scoreSelectedCallback?.onScoreSelected(location.first, location.second)
            } else {
                Toast.makeText(requireContext(), "No location for this score", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadDistances(): List<String> {
        val prefs = requireContext().getSharedPreferences("high_scores", Context.MODE_PRIVATE)
        val distances = prefs.getStringSet("distances", emptySet())!!.map { it.toInt() }.sortedDescending()
        return distances.map { "${it} m" }.padToSize(10, "-")
    }


    private fun List<String>.padToSize(size: Int, placeholder: String): List<String> {
        val result = this.toMutableList()
        while (result.size < size) result.add(placeholder)
        return result
    }


    private fun getLocationForPosition(position: Int): Pair<Double, Double>? {
        val prefs = requireContext().getSharedPreferences("high_scores", Context.MODE_PRIVATE)
        val latitudes = prefs.getStringSet("latitudes", emptySet())!!.toList()
        val longitudes = prefs.getStringSet("longitudes", emptySet())!!.toList()

        return if (position < latitudes.size && position < longitudes.size) {
            val lat = latitudes[position].toDoubleOrNull() ?: 0.0
            val lon = longitudes[position].toDoubleOrNull() ?: 0.0
            Pair(lat, lon)
        } else {
            null
        }
    }


    fun setScoreSelectedCallback(callback: Callback_HighScoreItemClicked) {
        this.scoreSelectedCallback = callback
    }
}
