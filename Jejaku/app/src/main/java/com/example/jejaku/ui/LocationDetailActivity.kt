package com.example.jejaku.ui

import android.os.Bundle
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.jejaku.data.AppDatabase
import com.example.jejaku.databinding.ActivityLocationDetailBinding
import kotlinx.coroutines.launch

/**
 * Location Detail Activity
 * Displays location on map with detailed information
 */
class LocationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationDetailBinding
    private var locationId: Long = -1
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    companion object {
        const val EXTRA_LOCATION_ID = "extra_location_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityLocationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupToolbar()
        setupWebView()

        // Get location ID from intent
        locationId = intent.getLongExtra(EXTRA_LOCATION_ID, -1)

        if (locationId != -1L) {
            loadLocationData()
        } else {
            finish()
        }

        setupFAB()
    }

    /**
     * Handle window insets for edge-to-edge display
     */
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            // Apply padding to AppBarLayout
            binding.appBarLayout.setPadding(
                insets.left,
                insets.top,
                insets.right,
                0
            )

            // Apply padding to card info for navigation bar
            binding.cardInfo.setPadding(
                binding.cardInfo.paddingLeft,
                binding.cardInfo.paddingTop,
                binding.cardInfo.paddingRight,
                insets.bottom
            )

            windowInsets
        }
    }

    /**
     * Setup toolbar with back button
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
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
     * Setup FAB to center map on location
     */
    private fun setupFAB() {
        binding.fabCenterMap.setOnClickListener {
            if (latitude != 0.0 && longitude != 0.0) {
                updateMapLocation(latitude, longitude)
            }
        }
    }

    /**
     * Load location data from database
     */
    private fun loadLocationData() {
        lifecycleScope.launch {
            val dao = AppDatabase.getDatabase(applicationContext).locationDao()
            val location = dao.getById(locationId)

            location?.let {
                // Update UI
                binding.tvLocationName.text = it.locationName
                binding.tvDate.text = it.date
                binding.tvNote.text = it.note
                binding.tvCoordinates.text = "Koordinat: ${it.latitude}, ${it.longitude}"

                // Store coordinates
                latitude = it.latitude
                longitude = it.longitude

                // Wait for WebView to load, then update map
                binding.webViewMap.postDelayed({
                    updateMapLocation(latitude, longitude)
                }, 1000)
            }
        }
    }

    /**
     * Update map with location marker
     */
    private fun updateMapLocation(lat: Double, lng: Double) {
        binding.webViewMap.evaluateJavascript(
            "updateLocation($lat, $lng);",
            null
        )
    }
}
