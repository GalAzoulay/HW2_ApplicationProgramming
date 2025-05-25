package com.example.hw2

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.hw2.R
import com.example.hw2.ui.HighScoresFragment
import com.example.hw2.ui.HighScoresMapFragment
import com.example.hw2.interfaces.Callback_HighScoreItemClicked


class HighScoresActivity : AppCompatActivity() {

    private lateinit var frameList: FrameLayout
    private lateinit var frameMap: FrameLayout

    private lateinit var scoresFragment: HighScoresFragment
    private lateinit var mapFragment: HighScoresMapFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)
        findViews()
        initFragments()
    }


    private fun findViews() {
        frameList = findViewById(R.id.high_scores_FRAME_list)
        frameMap = findViewById(R.id.high_scores_FRAME_map)
    }


    private fun initFragments() {
        mapFragment = HighScoresMapFragment()
        scoresFragment = HighScoresFragment()
        scoresFragment.setScoreSelectedCallback(object : Callback_HighScoreItemClicked {
            override fun onScoreSelected(lat: Double, lon: Double) {
                mapFragment.zoomToLocation(lat, lon)
            }
        })

        supportFragmentManager.beginTransaction()
            .replace(R.id.high_scores_FRAME_list, scoresFragment)
            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.high_scores_FRAME_map, mapFragment)
            .commit()
    }
}