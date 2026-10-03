package com.mrt1.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrt1.dashboard.can.CanStatus
import com.mrt1.dashboard.data.VehicleDataRepository
import com.mrt1.dashboard.ui.components.SpeedometerGauge
import com.mrt1.dashboard.ui.components.TachometerGauge

@Composable
fun DashboardScreen(
    repository: VehicleDataRepository,
    canStatus: CanStatus,
    onOpenDiagnostic: () -> Unit
) {
    val data by repository.telemetry.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF030712))
                )
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(8.dp))
                        .clickable { onOpenDiagnostic() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    val statusText = when (canStatus) {
                        is CanStatus.Connected -> "CAN: ${canStatus.providerName}"
                        is CanStatus.Disconnected -> "CAN: Connecting..."
                        is CanStatus.Error -> "CAN: Unavailable"
                    }
                    val statusColor = when (canStatus) {
                        is CanStatus.Connected -> Color(0xFF00E676)
                        is CanStatus.Disconnected -> Color(0xFFFFD600)
                        is CanStatus.Error -> Color(0xFFFF5252)
                    }
                    Text(text = statusText, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (data.isLeftTurn) Text("◀", color = Color(0xFF00E676), fontSize = 18.sp)
                    if (data.isParkingBrakeEngaged) Text("(P)", color = Color(0xFFFF5252), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    if (data.isDoorAjar) Text("DOOR", color = Color(0xFFFFD600), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    if (data.isHeadlightOn) Text("D", color = Color(0xFF00E5FF), fontSize = 14.sp)
                    if (data.isRightTurn) Text("▶", color = Color(0xFF00E676), fontSize = 18.sp)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpeedometerGauge(speed = data.speedKmh)

                Box(
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight(0.85f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x18FFFFFF))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "PEUGEOT PARS",
                            color = Color(0x99FFFFFF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${data.coolantTempC.toInt()}°C",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text("COOLANT", color = Color(0x66FFFFFF), fontSize = 9.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("${data.batteryVoltage}V", color = Color.White, fontSize = 14.sp)
                                Text("BATTERY", color = Color(0x66FFFFFF), fontSize = 8.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${data.fuelLevelPct.toInt()}%", color = Color.White, fontSize = 14.sp)
                                Text("FUEL", color = Color(0x66FFFFFF), fontSize = 8.sp)
                            }
                        }
                    }
                }

                TachometerGauge(rpm = data.rpm)
            }
        }
    }
}
