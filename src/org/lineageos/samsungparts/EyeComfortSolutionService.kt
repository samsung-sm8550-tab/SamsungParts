/*
 * Copyright (c) 2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

 package org.lineageos.samsungparts

 import android.app.Service
 import android.content.ContentResolver
 import android.content.Intent
 import android.database.ContentObserver
 import android.hardware.Sensor
 import android.hardware.SensorManager
 import android.os.*
 import android.provider.Settings
 import android.util.Log
 import java.io.File
 import java.io.IOException
 
 /**
  * Service that manages Eye Comfort/Night Dim settings for Samsung devices.
  */
 class EyeComfortSolutionService : Service() {
 
     private val tag = "EyeComfortSolutionService"
 
     // Sysfs node paths for Night Dim (Secondary node for foldables)
     private val nightDimNode = "/sys/class/lcd/panel/night_dim"
     private val nightDimFoldableNode = "/sys/class/lcd/panel1/night_dim"
 
     private var isFoldable = false
 
     override fun onCreate() {
         super.onCreate()
         Log.i(tag, "EyeComfortSolutionService started")
 
         isFoldable = checkIfDeviceIsFoldable()
         Log.i(tag, "Device foldable status: $isFoldable")
 
         registerSettingsObserver()
         applyNightDim()
     }
 
     private fun registerSettingsObserver() {
         contentResolver.registerContentObserver(
             Settings.Secure.getUriFor(Settings.Secure.NIGHT_DISPLAY_ACTIVATED),
             false, SettingsObserver(Handler(Looper.getMainLooper()))
         )
     }
 
     private fun applyNightDim() {
         val enabled = Settings.Secure.getInt(
             contentResolver, Settings.Secure.NIGHT_DISPLAY_ACTIVATED, 0
         ) == 1
         val value = if (enabled) "1" else "0"
 
         when {
             !isFoldable && writeNode(nightDimNode, value) ->
                 Log.i(tag, "Applied Night Dim: $value to primary display")
 
             isFoldable && writeNode(nightDimFoldableNode, value) ->
                 Log.i(tag, "Applied Night Dim: $value to foldable secondary display")
 
             else -> Log.w(tag, "No valid Night Dim node found. Skipping write.")
         }
     }
 
     private fun checkIfDeviceIsFoldable(): Boolean {
         val sensorManager = getSystemService(SensorManager::class.java)
         return sensorManager?.getSensorList(Sensor.TYPE_ALL)?.any {
             it.stringType == "android.sensor.fold_state"
         } ?: false
     }
 
     private fun writeNode(path: String, value: String): Boolean {
         val file = File(path)
         if (!file.exists() || !file.canWrite()) {
             Log.w(tag, "Write failed: $path does not exist or is not writable")
             return false
         }
         return try {
             file.writeText("$value\n")
             true
         } catch (e: IOException) {
             Log.e(tag, "Failed to write to $path", e)
             false
         }
     }
 
     private inner class SettingsObserver(handler: Handler) : ContentObserver(handler) {
         override fun onChange(selfChange: Boolean) {
             applyNightDim()
         }
     }
 
     override fun onBind(intent: Intent?): IBinder? {
         return null
     }
 }
 