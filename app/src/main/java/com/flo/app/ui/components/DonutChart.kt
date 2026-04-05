package com.flo.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flo.app.data.model.CategorySpending
import com.flo.app.ui.theme.Primary
import com.flo.app.ui.theme.SurfaceVariant

// A set of distinct colors for categories
val chartColors = listOf(
    Color(0xFFF5A623), // amber - primary
    Color(0xFFFF6B6B), // red
    Color(0xFF4ECDC4), // teal
    Color(0xFF45B7D1), // blue
    Color(0xFF96CEB4), // green
    Color(0xFFDDA0DD), // plum
    Color(0xFFFFB347), // orange
)

@Composable
fun DonutChart(
    categories: List<CategorySpending>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 28.dp
) {
    if (categories.isEmpty()) return

    val total = categories.sumOf { it.amount }
    val proportions = categories.map { (it.amount / total).toFloat() }

    // Animate each segment
    val animatedProportions = proportions.mapIndexed { index, target ->
        val animated by animateFloatAsState(
            targetValue = target,
            animationSpec = tween(
                durationMillis = 1000,
                delayMillis = index * 100,
                easing = EaseOutCubic
            ),
            label = "donut_$index"
        )
        animated
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Donut
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = strokeWidth.toPx()
                val diameter = size.minDimension - stroke
                val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                val arcSize = Size(diameter, diameter)

                // Background ring
                drawArc(
                    color = SurfaceVariant,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Butt)
                )

                var startAngle = -90f
                animatedProportions.forEachIndexed { index, proportion ->
                    val sweep = proportion * 360f
                    drawArc(
                        color = chartColors[index % chartColors.size],
                        startAngle = startAngle,
                        sweepAngle = sweep - 2f, // 2f gap between segments
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Butt)
                    )
                    startAngle += sweep
                }
            }

            // Center text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${categories.size}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "categories",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Legend
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
//            categories.take(5).forEachIndexed { index, category ->
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .size(10.dp)
//                            .let {
//                                it.then(
//                                    Modifier.padding(0.dp)
//                                )
//                            }
//                    ) {
//                        Canvas(modifier = Modifier.fillMaxSize()) {
//                            drawCircle(color = chartColors[index % chartColors.size])
//                        }
//                    }
//                    Text(
//                        text = category.category,
//                        style = MaterialTheme.typography.labelLarge,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.weight(1f)
//                    )
//                    Text(
//                        text = "${category.percentage.toInt()}%",
//                        style = MaterialTheme.typography.labelLarge,
//                        color = chartColors[index % chartColors.size],
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
            categories.take(5).forEachIndexed { index, category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.size(8.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(color = chartColors[index % chartColors.size])
                        }
                    }
                    Text(
                        text = category.category,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${category.percentage.toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = chartColors[index % chartColors.size],
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}