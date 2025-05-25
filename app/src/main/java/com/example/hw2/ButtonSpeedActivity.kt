package com.example.hw2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class ButtonSpeedActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button_speed)

        val buttonFast = findViewById<Button>(R.id.buttonFast)
        val buttonSlow = findViewById<Button>(R.id.buttonSlow)

        buttonFast.setOnClickListener {
            startGameWithSpeed("fast")
        }

        buttonSlow.setOnClickListener {
            startGameWithSpeed("slow")
        }
    }


    private fun startGameWithSpeed(speedMode: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("MODE", "buttons")
        intent.putExtra("SPEED_MODE", speedMode)
        startActivity(intent)
        finish()
    }
}
