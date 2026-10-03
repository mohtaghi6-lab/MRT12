package com.mrt1.dashboard.can

import com.mrt1.dashboard.data.VehicleTelemetry

class PeugeotParsDecoder {

    fun decode(frame: CanFrame, current: VehicleTelemetry): VehicleTelemetry {
        var updated = current.copy(
            totalFramesReceived = current.totalFramesReceived + 1,
            lastFrameId = frame.frameId,
            lastFramePayload = frame.toHexString()
        )

        val d = frame.data
        when (frame.frameId) {
            0x0B6, 0x0F6 -> {
                if (d.size >= 4) {
                    val rawRpm = ((d[0].toInt() and 0xFF) shl 8) or (d[1].toInt() and 0xFF)
                    val rawSpeed = ((d[2].toInt() and 0xFF) shl 8) or (d[3].toInt() and 0xFF)
                    updated = updated.copy(
                        rpm = (rawRpm / 8).coerceIn(0, 8000),
                        speedKmh = (rawSpeed / 100f).coerceIn(0f, 260f)
                    )
                }
            }
            0x128 -> {
                if (d.isNotEmpty()) {
                    val temp = (d[0].toInt() and 0xFF) - 40f
                    val volt = if (d.size >= 2) (d[1].toInt() and 0xFF) * 0.1f else current.batteryVoltage
                    updated = updated.copy(
                        coolantTempC = temp.coerceIn(40f, 130f),
                        batteryVoltage = volt.coerceIn(9f, 16f)
                    )
                }
            }
            0x1A8 -> {
                if (d.isNotEmpty()) {
                    val status = d[0].toInt() and 0xFF
                    updated = updated.copy(
                        isLeftTurn = (status and 0x01) != 0,
                        isRightTurn = (status and 0x02) != 0,
                        isHeadlightOn = (status and 0x04) != 0,
                        isHighBeamOn = (status and 0x08) != 0,
                        isParkingBrakeEngaged = (status and 0x10) != 0,
                        isDoorAjar = (status and 0x20) != 0
                    )
                }
            }
        }
        return updated
    }
}
