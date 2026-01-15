package com.example.jejaku.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jejaku.databinding.ActivityHistoryBinding
import com.example.jejaku.ui.adapter.HistoryAdapter
import com.example.jejaku.viewmodel.LocationViewModel

/**
 * History Activity
 * Displays all location records with delete functionality
 */
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: LocationViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        setupEdgeToEdge()

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupActionBar()
        setupRecyclerView()
        observeData()
    }

    /**
     * Enable edge-to-edge display (draw behind status bar)
     */
    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    /**
     * Handle window insets for notch/punch hole compatibility
     */
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            // Apply top padding to AppBarLayout to avoid notch/status bar
            binding.appBarLayout.setPadding(
                insets.left,
                insets.top,
                insets.right,
                0
            )

            // Let RecyclerView handle bottom inset for navigation bar
            binding.recyclerViewHistory.setPadding(
                binding.recyclerViewHistory.paddingLeft,
                binding.recyclerViewHistory.paddingTop,
                binding.recyclerViewHistory.paddingRight,
                insets.bottom
            )

            windowInsets
        }
    }

    /**
     * Setup action bar with back button
     */
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Setup RecyclerView with adapter
     */
    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(
            onItemClick = { locationId ->
                // Navigate to detail activity
                navigateToDetail(locationId)
            },
            onDeleteClick = { locationId ->
                // Show delete confirmation
                showDeleteConfirmation(locationId)
            }
        )
        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = historyAdapter
        }
    }

    /**
     * Navigate to LocationDetailActivity
     */
    private fun navigateToDetail(locationId: Long) {
        val intent = Intent(this, LocationDetailActivity::class.java).apply {
            putExtra(LocationDetailActivity.EXTRA_LOCATION_ID, locationId)
        }
        startActivity(intent)
    }

    /**
     * Observe LiveData from ViewModel
     */
    private fun observeData() {
        viewModel.allLocations.observe(this) { locations ->
            if (locations.isEmpty()) {
                binding.recyclerViewHistory.visibility = View.GONE
                binding.tvEmptyState.visibility = View.VISIBLE
            } else {
                binding.recyclerViewHistory.visibility = View.VISIBLE
                binding.tvEmptyState.visibility = View.GONE
                historyAdapter.submitList(locations)
            }
        }
    }

    /**
     * Show confirmation dialog before deleting
     */
    private fun showDeleteConfirmation(locationId: Long) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Riwayat")
            .setMessage("Apakah Anda yakin ingin menghapus riwayat ini?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteLocation(locationId)
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
