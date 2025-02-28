// hardware/samsung-ext/packages/apps/SamsungParts/src/org/lineageos/samsungparts/SamsungPartsApp.kt

package org.lineageos.samsungparts

import android.app.Application
import android.content.Intent
import android.util.Log

/**
 * SamsungPartsApp - Main application class for SamsungParts.
 * Ensures required services are initialized at startup.
 */
class SamsungPartsApp : Application() {
    private val TAG = "SamsungPartsApp"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "SamsungPartsApp initialized")

        // Start SamsungPartsService on boot
        startService(Intent(this, SamsungPartsService::class.java))
        Log.d(TAG, "SamsungPartsService started from SamsungPartsApp")
    }
}
