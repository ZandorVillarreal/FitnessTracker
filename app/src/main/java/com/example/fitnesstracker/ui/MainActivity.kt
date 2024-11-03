package com.example.fitnesstracker.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fitnesstracker.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var bottomNavigationView: BottomNavigationView

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
        .build()

    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissionsAndRun()
    }

    private fun checkPermissionsAndRun() {
        if (permissionApproved()) {
            fitSignIn()
        } else {
            requestRuntimePermissions()
        }
    }

    private fun fitSignIn() {
        val googleAccount = getGoogleAccount()
        if (googleAccount != null && !GoogleSignIn.hasPermissions(googleAccount, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                googleAccount,
                fitnessOptions
            )
        } else {
            setupNavigation()  // Call the new method here
        }
    }

    // New method for setting up navigation
    private fun setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.nav_controller)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

    private fun permissionApproved(): Boolean {
        val locationPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        Log.d("MainActivity", "Location Permission Status: $locationPermission")
        return if (runningQOrLater) {
            locationPermission == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestRuntimePermissions() {
        val shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (shouldProvideRationale) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Location permission is required for this app",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Grant") {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE
                )
            }.show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Location permission granted")
                fitSignIn()  // Call fitSignIn again to proceed with navigation setup
            } else {
                Log.d("MainActivity", "Location permission denied")
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setupNavigation()  // Ensure navigation setup here as well
        } else {
            Toast.makeText(this, "Google Fit permission not granted", Toast.LENGTH_LONG).show()
        }
    }
}
