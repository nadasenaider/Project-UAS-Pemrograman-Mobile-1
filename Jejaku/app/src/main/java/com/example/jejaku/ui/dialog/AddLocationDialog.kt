package com.example.jejaku.ui.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.jejaku.databinding.DialogAddLocationBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dialog for adding a new location record
 */
class AddLocationDialog(
    private val latitude: Double,
    private val longitude: Double,
    private val onSave: (locationName: String, date: String, note: String) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddLocationBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddLocationBinding.inflate(LayoutInflater.from(requireContext()))

        // Set current date as default
        val calendar = Calendar.getInstance()
        selectedDate = formatDate(calendar)
        binding.etDate.setText(selectedDate)

        // Display location coordinates
        binding.tvLocationInfo.text = "Lokasi: Lat $latitude, Long $longitude"

        // Date picker
        binding.etDate.setOnClickListener {
            showDatePicker(calendar)
        }

        // Cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        // Save button
        binding.btnSave.setOnClickListener {
            saveLocation()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    /**
     * Show date picker dialog
     */
    private fun showDatePicker(calendar: Calendar) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = formatDate(calendar)
                binding.etDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    /**
     * Format date to DD/MM/YYYY
     */
    private fun formatDate(calendar: Calendar): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    /**
     * Validate and save location
     */
    private fun saveLocation() {
        val locationName = binding.etLocationName.text.toString().trim()
        val note = binding.etNote.text.toString().trim()

        // Validation
        if (locationName.isEmpty()) {
            Toast.makeText(requireContext(), "Nama lokasi harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(requireContext(), "Tanggal harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (note.isEmpty()) {
            Toast.makeText(requireContext(), "Catatan harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Save and dismiss
        onSave(locationName, selectedDate, note)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
