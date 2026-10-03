package com.mrt1.dashboard.data

data class VehicleTelemetry(
    val speedKmh: Float = 0f,
    val rpm: Int = 0,
    val coolantTempC: Float = 90f,
    val fuelLevelPct: Float = 75f,
    val batteryVoltage: Float = 13.8f,
    val isLeftTurn: Boolean = false,
    val isRightTurn: Boolean = false,
    val isHeadlightOn: Boolean = false,
    val isHighBeamOn: Boolean = false,
    val isParkingBrakeEngaged: Boolean = false,
    val isDoorAjar: Boolean = false,
    val isSeatbeltUnbuckled: Boolean = false,
    val totalFramesReceived: Long = 0L,
    val lastFrameId: Int = 0,
    val lastFramePayload: String = ""
)
