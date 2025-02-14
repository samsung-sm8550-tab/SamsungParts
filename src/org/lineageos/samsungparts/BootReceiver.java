/*
 * Copyright (c) 2021-2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

 package org.lineageos.samsungparts;

 import android.content.BroadcastReceiver;
 import android.content.Context;
 import android.content.Intent;
 import android.util.Log;
 
 public class BootReceiver extends BroadcastReceiver {
     private static final String TAG = "SamsungParts/BootReceiver";
 
     @Override
     public void onReceive(Context context, Intent intent) {
         if (Intent.ACTION_LOCKED_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction())) {
             Log.i(TAG, "Locked boot completed, starting SamsungPartsService...");
 
             try {
                 context.startService(new Intent(context, SamsungPartsService.class));
                 Log.i(TAG, "SamsungPartsService started successfully.");
             } catch (Exception e) {
                 Log.e(TAG, "Failed to start SamsungPartsService", e);
             }
         }
     }
 }
 