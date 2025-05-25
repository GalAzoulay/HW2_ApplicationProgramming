package com.example.hw2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.*
import android.view.Gravity
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import com.example.hw2.logic.GameManager
import com.example.hw2.utilities.Constants
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import com.google.android.material.textview.MaterialTextView
import com.example.hw2.utilities.SensorManagerHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.example.hw2.utilities.SingleSoundPlayer


class MainActivity : AppCompatActivity() {

    private lateinit var gameLayout: LinearLayout
    private lateinit var cellViews: Array<Array<ImageView>>
    private lateinit var hearts: Array<AppCompatImageView>
    private lateinit var distanceLabel: MaterialTextView
    private lateinit var sensorHelper: SensorManagerHelper
    private lateinit var gameLoopHandler: Handler
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val gameManager = GameManager()
    private var gameMode: String = "buttons"
    private var sensorMode = false
    private var gameSpeed: Long = 700
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var pendingDistanceToSave: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val controlLayout = findViewById<LinearLayout>(R.id.controlLayout)

        gameMode = intent.getStringExtra("MODE") ?: "buttons"

        val speedMode = intent.getStringExtra("SPEED_MODE")
        gameSpeed = when (speedMode) {
            "fast" -> 300L
            "slow" -> 700L
            else -> 700L
        }

        gameLayout = findViewById(R.id.gameLayout)
        distanceLabel = findViewById(R.id.main_LBL_distance)

        hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupGrid()
        updateLivesUI()
        drawMatrix()

        if (gameMode == "sensor") {
            controlLayout.visibility = View.GONE
            sensorHelper = SensorManagerHelper(
                this,
                onTiltDetected = { direction ->
                    gameManager.moveCar(direction)
                    drawMatrix()
                },
                onSpeedChanged = { newSpeed ->
                    gameSpeed = newSpeed
                }
            )
        }

        if (gameMode == "buttons") {
            findViewById<Button>(R.id.main_BTN_left).setOnClickListener {
                gameManager.moveCar(-1)
                drawMatrix()
            }

            findViewById<Button>(R.id.main_BTN_right).setOnClickListener {
                gameManager.moveCar(1)
                drawMatrix()
            }
        }

        startGameLoop()
    }


    private fun setupGrid() {
        val cellSize = Resources.getSystem().displayMetrics.widthPixels / (Constants.GameLogic.numCols + 1)

        cellViews = Array(Constants.GameLogic.numRows) { row ->
            val rowLayout = LinearLayout(this)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.gravity = Gravity.CENTER

            val rowCells = Array(Constants.GameLogic.numCols) { col ->
                val cell = ImageView(this)
                val params = LinearLayout.LayoutParams(cellSize, cellSize)
                params.setMargins(6, 6, 6, 6)
                cell.layoutParams = params
                rowLayout.addView(cell)
                cell
            }
            gameLayout.addView(rowLayout)
            rowCells
        }
    }


    private fun startGameLoop() {
        gameLoopHandler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                gameManager.updateObstacles()
                val collisionType = gameManager.checkCollision()
                if (collisionType != Constants.Entity.EMPTY) {
                    val isGameOver = gameManager.handleCollision(collisionType)

                    when (collisionType) {
                        Constants.Entity.SEAL -> {
                            Toast.makeText(applicationContext,
                                "Got bit by a seal!",
                                Toast.LENGTH_SHORT
                            ).show()
                            vibrate()
                            SingleSoundPlayer(this@MainActivity).playSound(R.raw.crash_sound)
                            updateLivesUI()
                        }
                    }

                    if (isGameOver) {
                        gameLoopHandler.removeCallbacksAndMessages(null)
                        pendingDistanceToSave = gameManager.distanceTraveled
                        requestFreshLocation()
                        return
                    }
                }

                updateDistanceUI()
                drawMatrix()
                gameLoopHandler.postDelayed(this, gameSpeed)
            }
        }
        gameLoopHandler.post(runnable)
    }


    private fun drawMatrix() {
        for (i in 0 until Constants.GameLogic.numRows) {
            for (j in 0 until Constants.GameLogic.numCols) {
                val cell = cellViews[i][j]
                val isCar = (i == Constants.GameLogic.numRows - 1 && j == gameManager.carPosition)
                val isObstacle = gameManager.roadMatrix[i][j] == Constants.Entity.SEAL
                val isFish = gameManager.roadMatrix[i][j] == Constants.Entity.FISH
                when {
                    isCar -> cell.setImageResource(R.drawable.penguin)
                    isObstacle -> cell.setImageResource(R.drawable.seal)
                    isFish -> cell.setImageResource(R.drawable.fish)
                    else -> cell.setImageDrawable(null)
                }
            }
        }
    }


    private fun updateLivesUI() {
        for (i in hearts.indices) {
            hearts[i].visibility =
                if (i < gameManager.lives) ImageView.VISIBLE else ImageView.INVISIBLE
        }
    }


    private fun updateDistanceUI() {
        distanceLabel.text = "Distance: ${gameManager.distanceTraveled}"
    }


    private fun requestFreshLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
                .setWaitForAccurateLocation(true)
                .setMaxUpdates(1)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    val latitude = location?.latitude ?: 0.0
                    val longitude = location?.longitude ?: 0.0

                    fusedLocationClient.removeLocationUpdates(this)

                    saveScore(pendingDistanceToSave, latitude, longitude)
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestFreshLocation()
            } else {
                saveScore(pendingDistanceToSave, 32.11500604538742, 34.81808243687629)
            }
        }
    }


    private fun saveScore(distance: Int, latitude: Double, longitude: Double) {
        val prefs = getSharedPreferences("high_scores", Context.MODE_PRIVATE)
        val distances = prefs.getStringSet("distances", emptySet())!!.map { it.toInt() }.toMutableList()
        val latitudes = prefs.getStringSet("latitudes", emptySet())!!.toMutableList()
        val longitudes = prefs.getStringSet("longitudes", emptySet())!!.toMutableList()

        distances.add(distance)
        latitudes.add(latitude.toString())
        longitudes.add(longitude.toString())

        val combined = distances.mapIndexed { index, dist ->
            Triple(dist, latitudes.getOrNull(index) ?: "0.0", longitudes.getOrNull(index) ?: "0.0")
        }.sortedByDescending { it.first }.take(10)

        prefs.edit()
            .putStringSet("distances", combined.map { it.first.toString() }.toSet())
            .putStringSet("latitudes", combined.map { it.second }.toSet())
            .putStringSet("longitudes", combined.map { it.third }.toSet())
            .apply()

        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra("SCORE", distance)
        startActivity(intent)
        finish()
    }


    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
    }


    override fun onResume() {
        super.onResume()
        if (gameMode == "sensor") sensorHelper.register()
    }


    override fun onPause() {
        super.onPause()
        if (gameMode == "sensor") sensorHelper.unregister()
    }


    override fun onStop() {
        super.onStop()
        if (::gameLoopHandler.isInitialized) {
            gameLoopHandler.removeCallbacksAndMessages(null)
        }
    }
}