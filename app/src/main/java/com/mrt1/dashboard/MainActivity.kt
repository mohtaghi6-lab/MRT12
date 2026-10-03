package com.mrt1.dashboard

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mrt1.dashboard.can.CanProviderDiscovery
import com.mrt1.dashboard.data.VehicleDataRepository
import com.mrt1.dashboard.ui.DashboardScreen
import com.mrt1.dashboard.ui.DiagnosticScreen

class MainActivity : ComponentActivity() {

    private lateinit var discovery: CanProviderDiscovery
    private lateinit var repository: VehicleDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemBars()

        discovery = CanProviderDiscovery(applicationContext)
        repository = VehicleDataRepository(discovery)

        setContent {
            val canStatus by discovery.activeStatus.collectAsState()
            var showDiagnostic by remember { mutableStateOf(false) }

            if (showDiagnostic) {
                DiagnosticScreen(
                    repository = repository,
                    canStatus = canStatus,
                    onBack = { showDiagnostic = false }
                )
            } else {
                DashboardScreen(
                    repository = repository,
                    canStatus = canStatus,
                    onOpenDiagnostic = { showDiagnostic = true }
                )
            }
        }
    }

    private fun hideSystemBars() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        discovery.activeProvider?.disconnect()
    }
}
