package com.example.hw2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.hw2.R
import com.example.hw2.HighScoresActivity


class MenuActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val buttonModeButton = findViewById<Button>(R.id.menu_BTN_buttons)
        val sensorModeButton = findViewById<Button>(R.id.menu_BTN_sensor)
        val bestScoresButton = findViewById<Button>(R.id.menu_BTN_best_scores)

//        clearHighScoresOnce()

        bestScoresButton.setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)
        }

        buttonModeButton.setOnClickListener {
            val intent = Intent(this, ButtonSpeedActivity::class.java)
            startActivity(intent)
        }

        sensorModeButton.setOnClickListener {
            startGameWithMode("sensor")
        }
    }

//    private fun clearHighScoresOnce() {
//        val prefs = getSharedPreferences("high_scores", Context.MODE_PRIVATE)
//        prefs.edit().clear().apply() // Fully clears all keys in this preferences file
//    }

    private fun startGameWithMode(mode: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("MODE", mode)
        startActivity(intent)
    }
}