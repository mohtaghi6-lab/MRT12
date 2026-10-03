package com.mrt1.dashboard.can

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class XyAutoCanProvider(private val context: Context) : CanProvider {
    override val name: String = "XY Auto J6.8 CAN Pro"
    override val protocol: String = "CAN Pro 3.7 / MCU 4000"

    private val _frameStream = MutableSharedFlow<CanFrame>(extraBufferCapacity = 64)
    override val frameStream = _frameStream.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private var isListening = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent ?: return
            when (intent.action) {
                "com.xyauto.canbus.RECV_CAN_DATA" -> {
                    val frameId = intent.getIntExtra("id", -1)
                    val rawData = intent.getByteArrayExtra("data")
                    if (frameId != -1 && rawData != null) {
                        scope.launch {
                            _frameStream.emit(CanFrame(frameId, rawData))
                        }
                    }
                }
                "com.xyauto.mcu.DATA_CHANGED" -> {
                    val rawBytes = intent.getByteArrayExtra("mcu_bytes")
                    if (rawBytes != null && rawBytes.size >= 5) {
                        val frameId = ((rawBytes[0].toInt() and 0xFF) shl 8) or (rawBytes[1].toInt() and 0xFF)
                        val payload = rawBytes.copyOfRange(2, rawBytes.size)
                        scope.launch {
                            _frameStream.emit(CanFrame(frameId, payload))
                        }
                    }
                }
            }
        }
    }

    override suspend fun probe(): Boolean {
        val hasXyProp = Build.DISPLAY.contains("XYAUTO", ignoreCase = true) ||
                Build.FINGERPRINT.contains("xyauto", ignoreCase = true)
        val packageManager = context.packageManager
        val hasPackage = try {
            packageManager.getPackageInfo("com.xyauto.canbus", 0) != null
        } catch (e: Exception) {
            false
        }
        return hasXyProp || hasPackage
    }

    override suspend fun connect(): Boolean {
        if (isListening) return true
        val filter = IntentFilter().apply {
            addAction("com.xyauto.canbus.RECV_CAN_DATA")
            addAction("com.xyauto.mcu.DATA_CHANGED")
            addAction("com.microntek.canbusdisplay")
        }
        context.registerReceiver(receiver, filter)
        isListening = true
        return true
    }

    override fun disconnect() {
        if (isListening) {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
            isListening = false
        }
    }
}
