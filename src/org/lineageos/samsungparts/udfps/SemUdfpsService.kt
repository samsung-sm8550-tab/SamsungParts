/*
 * Copyright (c) 2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

package org.lineageos.samsungparts.udfps

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log

/**
 * Service for handling UDFPS (Under-Display Fingerprint Sensor) operations.
 */
class SemUdfpsService : Service() {
    private val TAG = "SemUdfpsService"
    private val udfpsHelper = SemUdfpsHelper.instance

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "SemUdfpsService created")
        udfpsHelper.onBootActivityManagerReady(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "SemUdfpsService onBind() called")
        return LocalBinder()
    }

    /**
     * Local binder for providing service access.
     */
    inner class LocalBinder : Binder() {
        fun getService(): SemUdfpsService = this@SemUdfpsService
    }

    /**
     * Returns the in-display sensor area details.
     */
    fun getInDisplaySensorArea(): Bundle {
        Log.d(TAG, "getInDisplaySensorArea() requested")
        return udfpsHelper.getInDisplaySensorArea()
    }

    companion object {
        /**
         * Starts the SemUdfpsService.
         */
        fun startService(context: Context) {
            val intent = Intent(context, SemUdfpsService::class.java)
            context.startService(intent)
            Log.d("SemUdfpsService", "Service started")
        }

        /**
         * Stops the SemUdfpsService.
         */
        fun stopService(context: Context) {
            val intent = Intent(context, SemUdfpsService::class.java)
            context.stopService(intent)
            Log.d("SemUdfpsService", "Service stopped")
        }
    }
}
