package com.mrt1.dashboard.can

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class SerialMcuCanProvider : CanProvider {
    override val name: String = "Direct MCU Serial (/dev/ttyMT1)"
    override val protocol: String = "Binary Frame 115200 8N1"

    private val _frameStream = MutableSharedFlow<CanFrame>(extraBufferCapacity = 64)
    override val frameStream = _frameStream.asSharedFlow()

    private var inputStream: InputStream? = null
    private var job: Job? = null
    private val candidatePorts = listOf("/dev/ttyMT1", "/dev/ttyMT2", "/dev/ttyS1", "/dev/ttyS2")

    override suspend fun probe(): Boolean = withContext(Dispatchers.IO) {
        candidatePorts.any { path ->
            val file = File(path)
            file.exists() && file.canRead()
        }
    }

    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        val selectedPath = candidatePorts.firstOrNull { File(it).canRead() } ?: return@withContext false
        try {
            val file = File(selectedPath)
            inputStream = FileInputStream(file)
            startReading()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun startReading() {
        job = CoroutineScope(Dispatchers.IO).launch {
            val stream = inputStream ?: return@launch
            val buffer = ByteArray(64)
            while (isActive) {
                val bytesRead = stream.read(buffer)
                if (bytesRead > 4 && buffer[0] == 0xAA.toByte()) {
                    val frameId = ((buffer[1].toInt() and 0xFF) shl 8) or (buffer[2].toInt() and 0xFF)
                    val len = buffer[3].toInt() and 0x0F
                    if (bytesRead >= 4 + len) {
                        val payload = buffer.copyOfRange(4, 4 + len)
                        _frameStream.emit(CanFrame(frameId, payload))
                    }
                }
            }
        }
    }

    override fun disconnect() {
        job?.cancel()
        try {
            inputStream?.close()
        } catch (_: Exception) {}
        inputStream = null
    }
}
