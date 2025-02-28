/*
 * Copyright (c) 2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

 package org.lineageos.samsungparts.udfps

 import android.content.Context
 import android.content.Intent
 import android.graphics.Point
 import android.graphics.Rect
 import android.os.Bundle
 import android.os.IBinder
 import android.util.Log
 import android.view.WindowManagerGlobal
 import java.io.File
 import java.nio.charset.StandardCharsets
 import org.lineageos.samsungparts.tsp.SecTspService
 
 /** UDFPS Helper for in-display fingerprint handling in LineageOS SamsungParts. */
 class SemUdfpsHelper private constructor(private val sysFsProvider: SemBioSysFsProvider) {
 
     private val TAG = "SemUdfpsHelper"
 
     private var sensorAreaWidth = "9"
     private var sensorAreaHeight = "4"
     private var sensorMarginBottom = "13.77"
     private var sensorMarginLeft = "0"
     private var sensorImageSize = "13.00"
     private var sensorActiveArea = "14.80"
     private var sensorDraggingArea = "5.00"
     private var tspService: SecTspService? = null
 
     companion object {
         val instance: SemUdfpsHelper by lazy {
             Log.d("SemUdfpsHelper", "Instance created")
             SemUdfpsHelper(DefaultSysFsProvider())
         }
     }
 
     fun onBootActivityManagerReady(context: Context) {
         Log.d(TAG, "onBootActivityManagerReady() called")
         readSensorAreaFromSysFs()
         bindToTspService(context)
     }
 
     private fun bindToTspService(context: Context) {
         Log.d(TAG, "Binding to SecTspService")
 
         val serviceConnection =
             object : android.content.ServiceConnection {
                 override fun onServiceConnected(
                     name: android.content.ComponentName?,
                     service: IBinder?
                 ) {
                     val binder = service as? SecTspService.LocalBinder
                     tspService = binder?.getService()
                     Log.d(TAG, "SecTspService connected")
                     setFodRect(context)
                 }
 
                 override fun onServiceDisconnected(name: android.content.ComponentName?) {
                     tspService = null
                     Log.w(TAG, "SecTspService disconnected")
                 }
             }
 
         val intent = Intent(context, SecTspService::class.java)
         context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
     }
 
     private fun readSensorAreaFromSysFs() {
         Log.d(TAG, "readSensorAreaFromSysFs() called")
         val readSysFs = sysFsProvider.readSysFs("/sys/class/fingerprint/fingerprint/position")
         Log.d(TAG, "SysFs Read: ${readSysFs?.let { String(it, StandardCharsets.UTF_8) } ?: "null"}")
 
         if (readSysFs != null) {
             try {
                 val split = String(readSysFs, StandardCharsets.UTF_8).trim().split(",")
                 Log.d(TAG, "Split data: $split")
 
                 if (split.size >= 8) {
                     sensorMarginBottom = split[0]
                     sensorMarginLeft = split[1]
                     sensorAreaWidth = split[2]
                     sensorAreaHeight = split[3]
                     sensorImageSize = split[7]
                     sensorActiveArea = split[5]
                     sensorDraggingArea = split[8]
                 }
 
                 Log.i(TAG, "readSensorConfig: $sensorAreaWidth x $sensorAreaHeight")
             } catch (e: Exception) {
                 Log.w(TAG, "readSensorConfig failed: ${e.message}")
             }
         }
     }
 
     private fun setFodRect(context: Context) {
         Log.d(TAG, "setFodRect() called")
 
         val point = Point()
         try {
             val windowManager = WindowManagerGlobal.getWindowManagerService()
             if (windowManager != null) {
                 windowManager.getInitialDisplaySize(0, point)
             } else {
                 Log.w(TAG, "WindowManagerGlobal is null, cannot retrieve screen size")
                 return
             }
 
             val rect = Rect()
             val xdpi = context.resources.displayMetrics.xdpi
 
             val activeArea = sensorActiveArea.toDouble() * xdpi * 0.03937007859349251
             val marginBottom = sensorMarginBottom.toDouble() * xdpi * 0.03937007859349251
             val marginLeft = sensorMarginLeft.toDouble() * xdpi * 0.03937007859349251
             val areaHeight = sensorAreaHeight.toDouble() * xdpi * 0.03937007859349251
 
             val rectSize = activeArea.toInt()
             val left = (point.x / 2) - ((rectSize / 2) - marginLeft.toInt())
             val top = point.y - (marginBottom.toInt() + (areaHeight.toInt() / 2) + (rectSize / 2))
             val right = left + rectSize
             val bottom = top + rectSize
 
             rect.set(left, top, right, bottom)
 
             val fodRectCommand = "set_fod_rect,$left,$top,$right,$bottom"
 
             Log.d(
                 TAG,
                 """
                 FOD Rect Calculation:
                 Screen Size: ${point.x}x${point.y}
                 DPI: $xdpi
                 Calculated Rect: Left=$left, Top=$top, Right=$right, Bottom=$bottom
                 Sending Command: $fodRectCommand
             """
                     .trimIndent()
             )
 
             tspService?.let {
                 val success = it.sendCommand(fodRectCommand)
                 Log.d(TAG, "Sent FOD Rect command: ${if (success) "Success" else "Failed"}")
             } ?: Log.w(TAG, "TSP Service not connected, unable to send FOD Rect command")
         } catch (e: Exception) {
             Log.w(TAG, "setFodRect failed: ${e.message}")
         }
     }
 
     fun getInDisplaySensorArea(): Bundle {
         Log.d(TAG, "getInDisplaySensorArea() called")
         return getInDisplaySensorArea(Bundle())
     }
 
     fun getInDisplaySensorArea(bundle: Bundle): Bundle {
         Log.d(TAG, "getInDisplaySensorArea(bundle) called")
         bundle.putStringArray(
             "sensor_area",
             arrayOf(
                 sensorAreaWidth,
                 sensorAreaHeight,
                 sensorMarginBottom,
                 sensorMarginLeft,
                 sensorImageSize,
                 sensorActiveArea,
                 sensorDraggingArea
             )
         )
         Log.d(TAG, "Bundle data: ${bundle.getStringArray("sensor_area")?.joinToString(", ")}")
         return bundle
     }
 }
 
 /** Default implementation of `SemBioSysFsProvider` for reading/writing SysFS nodes. */
 class DefaultSysFsProvider : SemBioSysFsProvider {
     override fun readSysFs(path: String): ByteArray? {
         Log.d("DefaultSysFsProvider", "readSysFs() called with path: $path")
 
         return try {
             val file = File(path)
             if (!file.exists()) {
                 Log.w("DefaultSysFsProvider", "File does not exist: $path")
                 return null
             }
             val data = file.readBytes()
             Log.d("DefaultSysFsProvider", "Read ${data.size} bytes from $path")
             data
         } catch (e: Exception) {
             Log.w("DefaultSysFsProvider", "Failed to read from $path: ${e.message}")
             null
         }
     }
 
     override fun writeSysFs(path: String, data: ByteArray) {
         try {
             val file = File(path)
             if (!file.exists()) {
                 Log.w("DefaultSysFsProvider", "File does not exist: $path")
                 return
             }
             file.writeBytes(data)
             Log.d("DefaultSysFsProvider", "Successfully wrote to $path")
         } catch (e: Exception) {
             Log.w("DefaultSysFsProvider", "Failed to write to $path: ${e.message}")
         }
     }
 }
 