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

@Composable
fun TachometerGauge(
    rpm: Int,
    maxRpm: Int = 8000,
    modifier: Modifier = Modifier
) {
    val animatedRpm by animateFloatAsState(
        targetValue = rpm.toFloat(),
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "rpm"
    )

    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val startAngle = 135f
            val sweepAngle = 270f

            drawArc(
                color = Color(0x22FF9100),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            val progressSweep = (animatedRpm / maxRpm.toFloat()) * sweepAngle
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color(0xFF00E5FF), Color(0xFFFF9100), Color(0xFFFF1744)),
                    center = center
                ),
                startAngle = startAngle,
                sweepAngle = progressSweep.coerceAtLeast(1f),
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "${(animatedRpm / 1000f).let { "%.1f".format(it) }}",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "1/MIN x 1000",
                color = Color(0xFFFF9100),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 52.dp)
            )
        }
    }
}
