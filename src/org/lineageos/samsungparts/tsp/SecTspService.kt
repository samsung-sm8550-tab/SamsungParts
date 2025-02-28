/*
 * Copyright (c) 2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

 package org.lineageos.samsungparts.tsp

 import android.app.Service
 import android.content.Context
 import android.content.Intent
 import android.os.Binder
 import android.os.IBinder
 import android.util.Log
 
 /**
  * Background service for handling sec_tsp touchscreen commands.
  * Provides access to send commands, get results, and list available commands.
  */
 class SecTspService : Service() {
 
     private val tag = "SecTspService"
     private val binder = LocalBinder()
 
     override fun onCreate() {
         super.onCreate()
         Log.i(tag, "SecTspService started")
     }
 
     override fun onBind(intent: Intent?): IBinder {
         Log.i(tag, "SecTspService bound")
         return binder
     }
 
     override fun onDestroy() {
         Log.i(tag, "SecTspService stopped")
         super.onDestroy()
     }
 
     /**
      * Binder class to expose service methods to bound components.
      */
     inner class LocalBinder : Binder() {
         fun getService(): SecTspService = this@SecTspService
     }
 
     /**
      * Retrieves a list of available touchscreen commands.
      * @return Array of available commands.
      */
     fun getAvailableCommands(): Array<String> {
         return SecTspCommandManager.getAvailableCommands()
     }
 
     /**
      * Sends a sec_tsp command to the touchscreen panel.
      * @param command The command to send.
      * @return True if the command was sent successfully, false otherwise.
      */
     fun sendCommand(command: String): Boolean {
         return SecTspCommandManager.sendCommand(command)
     }
 
     /**
      * Retrieves the result of the last executed command.
      * @return The command result as a string, or null if unavailable.
      */
     fun getCommandResult(): String? {
         return SecTspCommandManager.getCommandResult()
     }
 
     companion object {
         /**
          * Starts the SecTspService.
          * @param context The application context.
          */
         fun startService(context: Context) {
             val intent = Intent(context, SecTspService::class.java)
             context.startService(intent)
             Log.i("SecTspService", "Service started")
         }
 
         /**
          * Stops the SecTspService.
          * @param context The application context.
          */
         fun stopService(context: Context) {
             val intent = Intent(context, SecTspService::class.java)
             context.stopService(intent)
             Log.i("SecTspService", "Service stopped")
         }
     }
 }
 