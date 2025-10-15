package com.example.unit5project5bitfitpart1

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unit5project5bitfitpart1.data.WellnessDatabase
import com.example.unit5project5bitfitpart1.data.WellnessEntry
import com.example.unit5project5bitfitpart1.databinding.ActivityMainBinding
import com.example.unit5project5bitfitpart1.ui.WellnessAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter = WellnessAdapter()
    private val PICK_IMAGE_REQUEST = 100
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar color and title for styling
        binding.root.setBackgroundColor(Color.parseColor("#F5F6FA"))
        title = "BitFit Wellness Tracker"

        binding.rvEntries.layoutManager = LinearLayoutManager(this)
        binding.rvEntries.adapter = adapter

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEntryActivity::class.java))
        }

        // Photo feature
        binding.btnAddPhoto?.setOnClickListener {
            openGallery()
        }

        val dao = WellnessDatabase.get(this).wellnessDao()
        lifecycleScope.launch {
            dao.getAllFlow().collectLatest { list ->
                adapter.submit(list)
                updateStats(list)
                binding.tvEmpty.visibility =
                    if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    /** ----------------------- STRETCH FEATURE 1 -----------------------
     *  Calculate average water cups and show trend indicator.
     */
    private fun updateStats(list: List<WellnessEntry>) {
        if (list.isNotEmpty()) {
            val avg = list.map { it.amountCups }.average()
            binding.tvStats.text = String.format(Locale.getDefault(),
                "Average: %.1f cups/day", avg)

            val trendText = when {
                list.size > 1 && list[0].amountCups > list.last().amountCups ->
                    "⬆️ Increasing trend"
                list.size > 1 && list[0].amountCups < list.last().amountCups ->
                    "⬇️ Decreasing trend"
                else -> "➡️ Stable trend"
            }
            binding.tvTrend.text = trendText
        } else {
            binding.tvStats.text = ""
            binding.tvTrend.text = ""
        }
    }

    /** ----------------------- STRETCH FEATURE 4 -----------------------
     *  Photo selection logic (store file path not bitmap)
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                savePhotoPath(uri)
                Toast.makeText(this, "Photo added successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePhotoPath(uri: Uri) {
        val dao = WellnessDatabase.get(this).wellnessDao()
        val filePath = getRealPathFromUri(uri)
        lifecycleScope.launch(Dispatchers.IO) {
            val entry = WellnessEntry(
                amountCups = 0,
                note = "Photo added: $filePath",
                createdAt = System.currentTimeMillis()
            )
            dao.insert(entry)
        }
    }

    private fun getRealPathFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val idx = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(idx)
        } ?: ""
    }
}
