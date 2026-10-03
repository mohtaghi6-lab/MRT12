package com.mrt1.dashboard.can

data class CanFrame(
    val frameId: Int,
    val data: ByteArray,
    val timestampMs: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CanFrame) return false
        return frameId == other.frameId && data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = frameId
        result = 31 * result + data.contentHashCode()
        return result
    }

    fun toHexString(): String =
        data.joinToString(" ") { "%02X".format(it) }
}
