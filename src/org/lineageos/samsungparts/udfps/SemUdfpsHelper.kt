package org.lineageos.samsungparts.udfps

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * UDFPS Helper for in-display fingerprint handling in LineageOS SamsungParts.
 */
class SemUdfpsHelper private constructor(private val injector: Injector) {

    private val TAG = "SemUdfpsHelper"
    private val DEBUG = true

    private var sensorAreaWidth = "9"
    private var sensorAreaHeight = "4"
    private var sensorMarginBottom = "13.77"
    private var sensorMarginLeft = "0"
    private var sensorImageSize = "13.00"
    private var sensorActiveArea = "14.80"
    private var sensorDraggingArea = "5.00"

    companion object {
        val instance: SemUdfpsHelper by lazy { 
            Log.d("SemUdfpsHelper", "Instance created")
            SemUdfpsHelper(Injector()) 
        }
    }

    fun onBootActivityManagerReady(context: Context) {
        Log.d(TAG, "onBootActivityManagerReady() called")
        readSensorAreaFromSysFs()
        setFodRect(context)
    }

    private fun readSensorAreaFromSysFs() {
        Log.d(TAG, "readSensorAreaFromSysFs() called")
        val readSysFs = injector.readSysFs("/sys/class/fingerprint/fingerprint/position")
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

        try {
            val displayMetrics = context.resources.displayMetrics
            Log.d(TAG, "Display Metrics: ${displayMetrics.xdpi} xdpi, ${displayMetrics.ydpi} ydpi")

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val screenSize = Point().apply { windowManager.defaultDisplay.getRealSize(this) }
            Log.d(TAG, "Screen Size: ${screenSize.x}x${screenSize.y}")

            val activeArea = sensorActiveArea.toDouble() * displayMetrics.xdpi * 0.03937
            val marginBottom = sensorMarginBottom.toDouble() * displayMetrics.xdpi * 0.03937
            val marginLeft = sensorMarginLeft.toDouble() * displayMetrics.xdpi * 0.03937
            val areaHeight = sensorAreaHeight.toDouble() * displayMetrics.xdpi * 0.03937

            Log.d(TAG, "Calculated values - Active Area: $activeArea, Margin Bottom: $marginBottom, Margin Left: $marginLeft, Area Height: $areaHeight")

            val rectSize = activeArea.toInt()
            val centerX = (screenSize.x / 2) - ((rectSize / 2) - marginLeft.toInt())
            val centerY = screenSize.y - (marginBottom.toInt() + (areaHeight.toInt() / 2) + (rectSize / 2))

            val fodRect = Rect(centerX, centerY, centerX + rectSize, centerY + rectSize)
            Log.d(TAG, "setFodRect: ${fodRect.toShortString()}")
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
            "sensor_area", arrayOf(
                sensorAreaWidth, sensorAreaHeight, sensorMarginBottom,
                sensorMarginLeft, sensorImageSize, sensorActiveArea, sensorDraggingArea
            )
        )
        Log.d(TAG, "Bundle data: ${bundle.getStringArray("sensor_area")?.joinToString(", ")}")
        return bundle
    }

    class Injector {
        fun readSysFs(path: String): ByteArray? {
            Log.d("Injector", "readSysFs() called with path: $path")

            return try {
                val file = File(path)
                if (!file.exists()) {
                    Log.w("Injector", "File does not exist: $path")
                    return null
                }
                val data = file.readBytes()
                Log.d("Injector", "Read ${data.size} bytes from $path")
                data
            } catch (e: Exception) {
                Log.w("Injector", "Failed to read from $path: ${e.message}")
                null
            }
        }
    }
}
