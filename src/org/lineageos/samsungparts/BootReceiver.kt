/*
 * Copyright (c) 2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

package org.lineageos.samsungparts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Receives boot completion broadcasts and starts SamsungPartsService.
 */
class BootReceiver : BroadcastReceiver() {
    private val tag = "SamsungParts/BootReceiver"

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.action, ignoreCase = true)) {
            Log.i(tag, "Locked boot completed, starting SamsungPartsService...")

            try {
                context.startService(Intent(context, SamsungPartsService::class.java))
                Log.i(tag, "SamsungPartsService started successfully.")
            } catch (e: Exception) {
                Log.e(tag, "Failed to start SamsungPartsService", e)
            }
        }
    }
}
