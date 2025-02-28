/*
 * Copyright (c) 2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

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
