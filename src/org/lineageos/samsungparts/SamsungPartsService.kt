/*
 * Copyright (c) 2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

package org.lineageos.samsungparts

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.lineageos.samsungparts.tsp.SecTspService
import org.lineageos.samsungparts.udfps.SemUdfpsService

class SamsungPartsService : Service() {
    private val TAG = "SamsungPartsService"

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "SamsungPartsService started")

        // Start EyeComfortSolutionService
        Log.i(TAG, "Starting EyeComfortSolutionService...")
        startService(Intent(this, EyeComfortSolutionService::class.java))

        // Start SecTspService
        Log.i(TAG, "Starting SecTspService...")
        startService(Intent(this, SecTspService::class.java))

        // Start SemUdfpsService
        Log.i(TAG, "Starting SemUdfpsService...")
        startService(Intent(this, SemUdfpsService::class.java))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
