/*
 * Copyright (c) 2021-2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

 package org.lineageos.samsungparts;

 import android.app.Service;
 import android.content.ContentResolver;
 import android.content.Intent;
 import android.database.ContentObserver;
 import android.hardware.Sensor;
 import android.hardware.SensorManager;
 import android.os.FileUtils;
 import android.os.Handler;
 import android.os.IBinder;
 import android.os.Looper;
 import android.provider.Settings;
 import android.util.Log;
 
 import java.io.File;
 import java.io.IOException;
 import java.nio.charset.StandardCharsets;
 import java.util.List;
 
 public class EyeComfortSolutionService extends Service {
     private static final String TAG = "EyeComfortSolutionService";
 
     // Sysfs node paths for Night Dim (Secondary node is for foldables)
     private static final String NIGHT_DIM_NODE = "/sys/class/lcd/panel/night_dim";
     private static final String NIGHT_DIM_FOLDABLE_NODE = "/sys/class/lcd/panel1/night_dim";
 
     private boolean isFoldable = false;
 
     @Override
     public void onCreate() {
         super.onCreate();
         Log.i(TAG, "EyeComfortSolutionService started");
 
         isFoldable = checkIfDeviceIsFoldable();
         Log.i(TAG, "Device foldable status: " + isFoldable);
 
         registerSettingsObserver();
         applyNightDim();
     }
 
     private void registerSettingsObserver() {
         ContentResolver resolver = getContentResolver();
         resolver.registerContentObserver(
             Settings.Secure.getUriFor(Settings.Secure.NIGHT_DISPLAY_ACTIVATED),
             false, new SettingsObserver(new Handler(Looper.getMainLooper()))
         );
     }
 
     private void applyNightDim() {
         boolean enabled = Settings.Secure.getInt(
             getContentResolver(), Settings.Secure.NIGHT_DISPLAY_ACTIVATED, 0) == 1;
         String value = enabled ? "1" : "0";
 
         if (!isFoldable && writeNode(NIGHT_DIM_NODE, value)) {
             Log.i(TAG, "Applied Night Dim: " + value + " to primary display");
         } else if (isFoldable && writeNode(NIGHT_DIM_FOLDABLE_NODE, value)) {
             Log.i(TAG, "Applied Night Dim: " + value + " to foldable secondary display");
         } else {
             Log.w(TAG, "No valid Night Dim node found. Skipping write.");
         }
     }
 
     private boolean checkIfDeviceIsFoldable() {
         SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
         if (sensorManager != null) {
             List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
             for (Sensor sensor : sensorList) {
                 if (sensor.getStringType().equals("android.sensor.fold_state")) {
                     return true;
                 }
             }
         }
         return false;
     }
 
     private boolean writeNode(String path, String value) {
         File file = new File(path);
         if (!file.exists() || !file.canWrite()) {
             return false;
         }
         try {
             FileUtils.stringToFile(path, value + "\n");
             return true;
         } catch (IOException e) {
             Log.e(TAG, "Failed to write to " + path, e);
             return false;
         }
     }
 
     private class SettingsObserver extends ContentObserver {
         public SettingsObserver(Handler handler) {
             super(handler);
         }
 
         @Override
         public void onChange(boolean selfChange) {
             applyNightDim();
         }
     }
 
     @Override
     public IBinder onBind(Intent intent) {
         return null;
     }
 }
 