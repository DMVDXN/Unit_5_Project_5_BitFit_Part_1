package com.example.unit5project5bitfitpart1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.unit5project5bitfitpart1.data.WellnessDatabase
import com.example.unit5project5bitfitpart1.databinding.ActivityStatsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class StatsActivity : ComponentActivity() {
    private lateinit var binding: ActivityStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = WellnessDatabase.get(this).wellnessDao()
        lifecycleScope.launch {
            dao.getAllFlow().collectLatest { list ->
                if (list.isEmpty()) {
                    binding.tvStats.text = "No data yet"
                    return@collectLatest
                }
                val now = System.currentTimeMillis()
                val sevenDaysAgo = now - TimeUnit.DAYS.toMillis(7)

                val last7 = list.filter { it.createdAt >= sevenDaysAgo }
                val avg7 = if (last7.isNotEmpty()) last7.map { it.amountCups }.average() else 0.0
                val avgAll = list.map { it.amountCups }.average()
                val todayTotal = list
                    .filter { android.text.format.DateFormat.format("yyyy-MM-dd", it.createdAt) ==
                            android.text.format.DateFormat.format("yyyy-MM-dd", now) }
                    .sumOf { it.amountCups }

                binding.tvStats.text =
                    "Average last 7 days: ${avg7.roundToInt()} cups\n" +
                            "Average all time: ${avgAll.roundToInt()} cups\n" +
                            "Today total: $todayTotal cups\n" +
                            "Entries last 7 days: ${last7.size}"
            }
        }
    }
}
