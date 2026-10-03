package com.mrt1.dashboard.data

import com.mrt1.dashboard.can.CanProviderDiscovery
import com.mrt1.dashboard.can.PeugeotParsDecoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VehicleDataRepository(
    private val discovery: CanProviderDiscovery,
    private val decoder: PeugeotParsDecoder = PeugeotParsDecoder()
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _telemetry = MutableStateFlow(VehicleTelemetry())
    val telemetry: StateFlow<VehicleTelemetry> = _telemetry.asStateFlow()

    init {
        scope.launch {
            val provider = discovery.discoverAndConnect()
            provider?.frameStream?.collectLatest { frame ->
                _telemetry.value = decoder.decode(frame, _telemetry.value)
            }
        }
    }
}
