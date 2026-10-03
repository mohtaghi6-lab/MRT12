package com.mrt1.dashboard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerGauge(
    speed: Float,
    maxSpeed: Float = 260f,
    modifier: Modifier = Modifier
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "speed"
    )

    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = size.minDimension
            val radius = diameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            val startAngle = 135f
            val sweepAngle = 270f

            drawArc(
                color = Color(0x2200E5FF),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            val progressSweep = (animatedSpeed / maxSpeed) * sweepAngle
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color(0xFF0055FF), Color(0xFF00E5FF), Color(0xFFFF4081)),
                    center = center
                ),
                startAngle = startAngle,
                sweepAngle = progressSweep.coerceAtLeast(1f),
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            val tickCount = 13
            for (i in 0..tickCount) {
                val tickAngle = Math.toRadians((startAngle + (i.toFloat() / tickCount) * sweepAngle).toDouble())
                val innerR = radius - 16.dp.toPx()
                val outerR = radius - 6.dp.toPx()

                val p1 = Offset(
                    x = center.x + (innerR * cos(tickAngle)).toFloat(),
                    y = center.y + (innerR * sin(tickAngle)).toFloat()
                )
                val p2 = Offset(
                    x = center.x + (outerR * cos(tickAngle)).toFloat(),
                    y = center.y + (outerR * sin(tickAngle)).toFloat()
                )

                drawLine(
                    color = Color(0x66FFFFFF),
                    start = p1,
                    end = p2,
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "${animatedSpeed.toInt()}",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "KM/H",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 56.dp)
            )
        }
    }
}
