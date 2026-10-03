package com.mrt1.dashboard.can

import kotlinx.coroutines.flow.SharedFlow

sealed class CanStatus {
    object Disconnected : CanStatus()
    data class Connected(val providerName: String, val protocol: String) : CanStatus()
    data class Error(val message: String) : CanStatus()
}

interface CanProvider {
    val name: String
    val protocol: String
    val frameStream: SharedFlow<CanFrame>
    suspend fun probe(): Boolean
    suspend fun connect(): Boolean
    fun disconnect()
}
