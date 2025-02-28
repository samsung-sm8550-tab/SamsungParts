/*
 * Copyright (c) 2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

 package org.lineageos.samsungparts.tsp

 import android.util.Log
 import java.io.File
 import java.io.IOException
 import java.nio.charset.StandardCharsets
 
 /**
  * Manages sec_tsp touchscreen commands, retrieves command results,
  * and lists available touchscreen commands from sysfs nodes.
  */
 object SecTspCommandManager {
 
     private const val TAG = "SecTspCommandManager"
     private const val SEC_TSP_PATH = "/sys/class/sec/tsp"
     private const val CMD_FILE = "$SEC_TSP_PATH/cmd"
     private const val CMD_RESULT_FILE = "$SEC_TSP_PATH/cmd_result"
     private const val CMD_LIST_FILE = "$SEC_TSP_PATH/cmd_list"
 
     /**
      * Retrieves a list of available sec_tsp commands from the sysfs node.
      * @return An array of available commands, or an empty array if an error occurs.
      */
     fun getAvailableCommands(): Array<String> {
         return try {
             val cmdListFile = File(CMD_LIST_FILE)
             if (cmdListFile.exists() && cmdListFile.canRead()) {
                 cmdListFile.readLines(StandardCharsets.UTF_8)
                     .map { it.trim() }
                     .filter { it.isNotEmpty() }
                     .toTypedArray().also {
                         Log.d(TAG, "Available commands: ${it.joinToString(", ")}")
                     }
             } else {
                 Log.w(TAG, "cmd_list file not accessible: $CMD_LIST_FILE")
                 emptyArray()
             }
         } catch (e: IOException) {
             Log.e(TAG, "Error reading cmd_list file", e)
             emptyArray()
         }
     }
 
     /**
      * Sends a command to the touchscreen panel by writing to the cmd file.
      * @param command The command to send.
      * @return True if the command was sent successfully, false otherwise.
      */
     fun sendCommand(command: String): Boolean {
         return try {
             val cmdFile = File(CMD_FILE)
             if (cmdFile.exists() && cmdFile.canWrite()) {
                 cmdFile.writeText(command + "\n", StandardCharsets.UTF_8)
                 Log.d(TAG, "Command sent: $command")
                 true
             } else {
                 Log.w(TAG, "cmd file not accessible: $CMD_FILE")
                 false
             }
         } catch (e: IOException) {
             Log.e(TAG, "Failed to send command: $command", e)
             false
         }
     }
 
     /**
      * Retrieves the last command result from the cmd_result file.
      * @return The command result as a string, or null if an error occurs.
      */
     fun getCommandResult(): String? {
         return try {
             val cmdResultFile = File(CMD_RESULT_FILE)
             if (cmdResultFile.exists() && cmdResultFile.canRead()) {
                 cmdResultFile.readText(StandardCharsets.UTF_8).trim().also {
                     Log.d(TAG, "Command result: $it")
                 }
             } else {
                 Log.w(TAG, "cmd_result file not accessible: $CMD_RESULT_FILE")
                 null
             }
         } catch (e: IOException) {
             Log.e(TAG, "Failed to read command result", e)
             null
         }
     }
 }
 