package com.example.jejaku.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.WebSettings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.jejaku.databinding.ActivityMapsBinding
import com.example.jejaku.ui.dialog.AddLocationDialog
import com.example.jejaku.viewmodel.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * Maps Activity - Main screen with Leaflet map and bottom navigation
 */
class MapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: LocationViewModel by viewModels()

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupWindowInsets()
        setupWebView()
        setupBottomNavigation()
        checkPermissionsAndGetLocation()
    }

    /**
     * Handle window insets for edge-to-edge display
     */
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            // Apply bottom padding to bottom navigation to avoid gesture bar
            binding.bottomNavigation.setPadding(
                insets.left,
                binding.bottomNavigation.paddingTop,
                insets.right,
                insets.bottom
            )

            windowInsets
        }
    }

    /**
     * Setup WebView for Leaflet map
     */
    private fun setupWebView() {
        binding.webViewMap.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }
            loadUrl("file:///android_asset/map.html")
        }
    }

    /**
     * Setup bottom navigation click listeners
     */
    private fun setupBottomNavigation() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Add button - show dialog to save current location
        binding.btnAdd.setOnClickListener {
            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                showAddLocationDialog()
            } else {
                Toast.makeText(this, "Lokasi belum terdeteksi", Toast.LENGTH_SHORT).show()
                checkPermissionsAndGetLocation()
            }
        }

        // History button
        binding.btnHistory.setOnClickListener {
            startActivity(android.content.Intent(this, HistoryActivity::class.java))
        }

        // FAB for current location
        binding.fabCurrentLocation.setOnClickListener {
            checkPermissionsAndGetLocation()
        }
    }

    /**
     * Check location permissions
     */
    private fun checkPermissionsAndGetLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    /**
     * Get current GPS location
     */
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude

                    // Update map
                    updateMapLocation(currentLatitude, currentLongitude)

                    Toast.makeText(this, "Lokasi terdeteksi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Tidak dapat mendeteksi lokasi", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error mendeteksi lokasi", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Update map with new coordinates
     */
    private fun updateMapLocation(latitude: Double, longitude: Double) {
        binding.webViewMap.evaluateJavascript(
            "updateLocation($latitude, $longitude);",
            null
        )
    }

    /**
     * Show dialog to add location with current coordinates
     */
    private fun showAddLocationDialog() {
        val dialog = AddLocationDialog(
            latitude = currentLatitude,
            longitude = currentLongitude,
            onSave = { locationName, date, note ->
                viewModel.insertLocation(currentLatitude, currentLongitude, locationName, date, note)
                Toast.makeText(this, "Lokasi berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
        )
        dialog.show(supportFragmentManager, "AddLocationDialog")
    }

    /**
     * Handle permission request result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
