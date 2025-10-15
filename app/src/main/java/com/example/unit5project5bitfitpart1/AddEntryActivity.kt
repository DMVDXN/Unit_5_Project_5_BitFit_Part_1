package com.example.unit5project5bitfitpart1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.unit5project5bitfitpart1.data.WellnessDatabase
import com.example.unit5project5bitfitpart1.data.WellnessEntry
import com.example.unit5project5bitfitpart1.databinding.ActivityAddEntryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEntryActivity : ComponentActivity() {

    private lateinit var binding: ActivityAddEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            val amountStr = binding.etAmount.text.toString().trim()
            val note = binding.etNote.text.toString().trim()

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Enter a number of cups", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val amount = amountStr.toIntOrNull()
            if (amount == null) {
                Toast.makeText(this, "Amount must be a whole number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val entry = WellnessEntry(
                    amountCups = amount,
                    note = note,
                    createdAt = System.currentTimeMillis()
                )
                WellnessDatabase.get(this@AddEntryActivity).wellnessDao().insert(entry)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddEntryActivity, "Saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
