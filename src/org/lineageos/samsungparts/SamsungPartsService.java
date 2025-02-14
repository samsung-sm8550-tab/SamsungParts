/*
 * Copyright (c) 2021-2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

 package org.lineageos.samsungparts;

 import android.app.Service;
 import android.content.Context;
 import android.content.Intent;
 import android.os.IBinder;
 import android.util.Log;
 
 public class SamsungPartsService extends Service {
     private static final String TAG = "SamsungPartsService";
 
     @Override
     public void onCreate() {
         super.onCreate();
         Log.i(TAG, "SamsungPartsService started");
 
         // Start EyeComfortSolutionService from here
         startService(new Intent(this, EyeComfortSolutionService.class));
     }
 
     @Override
     public IBinder onBind(Intent intent) {
         return null;
     }
 }
 