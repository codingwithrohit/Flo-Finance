package com.flo.app.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun SpendingLineChart(
    data: List<Pair<String, Double>>,  // label to amount
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val entries = data.mapIndexed { index, (_, amount) ->
        Entry(index.toFloat(), amount.toFloat())
    }
    val labels = data.map { it.first }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                // Chart styling
                setBackgroundColor(AndroidColor.TRANSPARENT)
                setDrawGridBackground(false)
                setDrawBorders(false)
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(false)
                setPinchZoom(false)
                setExtraOffsets(8f, 16f, 8f, 8f)

                // X axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    textColor = AndroidColor.parseColor("#A0A0A0")
                    textSize = 10f
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(labels)
                    labelRotationAngle = -45f
                }

                // Left Y axis
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = AndroidColor.parseColor("#1E1E1E")
                    setDrawAxisLine(false)
                    textColor = AndroidColor.parseColor("#A0A0A0")
                    textSize = 10f
                }

                // Right Y axis — disable
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val dataSet = LineDataSet(entries, "Spending").apply {
                color = AndroidColor.parseColor("#F5A623")
                lineWidth = 2.5f
                setDrawCircles(true)
                circleRadius = 4f
                setCircleColor(AndroidColor.parseColor("#F5A623"))
                circleHoleColor = AndroidColor.parseColor("#141414")
                circleHoleRadius = 2f
                setDrawFilled(true)
                fillColor = AndroidColor.parseColor("#F5A623")
                fillAlpha = 30
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.2f
                setDrawValues(false)
                highLightColor = AndroidColor.parseColor("#FFD97D")
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
            chart.animateX(800)
        }
    )
}