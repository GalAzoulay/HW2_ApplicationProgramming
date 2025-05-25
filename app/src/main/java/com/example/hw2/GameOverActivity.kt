package com.example.hw2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView


class GameOverActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val score = intent.getIntExtra("SCORE", 0)
        val scoreText = findViewById<TextView>(R.id.game_over_LBL_score)
        val menuButton = findViewById<Button>(R.id.game_over_BTN_menu)

        scoreText.text = "Your Score: $score m"

        menuButton.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }
}