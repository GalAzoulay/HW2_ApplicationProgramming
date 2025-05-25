package com.example.hw2.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast


class SensorManagerHelper(
    private val context: Context,
    private val onTiltDetected: (direction: Int) -> Unit,
    private val onSpeedChanged: (speedMillis: Long) -> Unit
) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


    fun register() {
        if (accelerometer == null) {
            Toast.makeText(context, "No accelerometer sensor found.", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
    }


    fun unregister() {
        sensorManager.unregisterListener(this)
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val tiltX = event.values[0]
        val tiltY = event.values[1]

        val direction = when {
            tiltX > 3 -> -1
            tiltX < -3 -> 1
            else -> 0
        }

        if (direction != 0) {
            onTiltDetected(direction)
        }

        val speed = calculateSpeedBasedOnTilt(tiltY)
        onSpeedChanged(speed)
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used, required by interface
    }


    private fun calculateSpeedBasedOnTilt(tiltY: Float): Long {
        return when {
            tiltY < 4 -> 300L
            tiltY > 7 -> 900L
            else -> 700L
        }
    }
}

