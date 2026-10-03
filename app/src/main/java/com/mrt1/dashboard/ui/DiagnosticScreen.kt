package com.mrt1.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrt1.dashboard.can.CanStatus
import com.mrt1.dashboard.data.VehicleDataRepository

@Composable
fun DiagnosticScreen(
    repository: VehicleDataRepository,
    canStatus: CanStatus,
    onBack: () -> Unit
) {
    val telemetry by repository.telemetry.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CAN-BUS DIAGNOSTICS & SYSTEM STATUS",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("BACK TO COCKPIT", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Target Vehicle: Peugeot Pars 1392/1393 (Multiplex BSI)", color = Color(0xFF00E5FF), fontSize = 13.sp)
                    Text("Target Hardware: XY Auto J6.8 / MCU 4000 (X2) / CAN Pro 3.7", color = Color.White, fontSize = 13.sp)
                    Text("Provider Status: ${when (canStatus) {
                        is CanStatus.Connected -> "${canStatus.providerName} (${canStatus.protocol})"
                        is CanStatus.Disconnected -> "Searching..."
                        is CanStatus.Error -> canStatus.message
                    }}", color = Color(0xFF00E676), fontSize = 13.sp)
                    Text("RX Frames Counter: ${telemetry.totalFramesReceived}", color = Color.White, fontSize = 13.sp)
                    Text("Last Frame ID: 0x%03X".format(telemetry.lastFrameId), color = Color.White, fontSize = 13.sp)
                    Text("Last Payload: [ ${telemetry.lastFramePayload} ]", color = Color.Yellow, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    Text("Decoded Telemetry: Speed=${telemetry.speedKmh} km/h | RPM=${telemetry.rpm} | Temp=${telemetry.coolantTempC}°C | Volt=${telemetry.batteryVoltage}V", color = Color(0xFF90CAF9), fontSize = 12.sp)
                }
            }
        }
    }
}
