package com.mrt1.dashboard.can

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CanProviderDiscovery(private val context: Context) {
    private val _activeStatus = MutableStateFlow<CanStatus>(CanStatus.Disconnected)
    val activeStatus: StateFlow<CanStatus> = _activeStatus.asStateFlow()

    var activeProvider: CanProvider? = null
        private set

    suspend fun discoverAndConnect(): CanProvider? {
        val candidates = listOf(
            XyAutoCanProvider(context),
            SerialMcuCanProvider()
        )

        for (provider in candidates) {
            if (provider.probe()) {
                val success = provider.connect()
                if (success) {
                    activeProvider = provider
                    _activeStatus.value = CanStatus.Connected(provider.name, provider.protocol)
                    return provider
                }
            }
        }

        _activeStatus.value = CanStatus.Error("No accessible CAN hardware provider detected")
        return null
    }
}
