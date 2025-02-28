/*
 * Copyright (c) 2025 The LineageOS Project
 * Licensed under the Apache License, Version 2.0
 */

package org.lineageos.samsungparts.udfps

import android.util.Slog
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * Utility functions for file operations.
 */
object Utils {
    private const val TAG = "Utils"

    /**
     * Reads the contents of a file as a byte array.
     *
     * @param file The file to read.
     * @return The file contents as a byte array, or null if an error occurs.
     */
    fun readFile(file: File): ByteArray? {
        return try {
            if (!file.exists() || !file.isFile) {
                Slog.w(TAG, "readFile: File does not exist or is not a valid file - ${file.path}")
                return null
            }
            file.readBytes()
        } catch (e: IOException) {
            Slog.e(TAG, "readFile failed: ${e.message}")
            null
        }
    }

    /**
     * Reads the contents of a file as a UTF-8 string.
     *
     * @param file The file to read.
     * @return The file contents as a String, or null if an error occurs.
     */
    fun readFileAsString(file: File): String? = readFile(file)?.toString(StandardCharsets.UTF_8)

    /**
     * Writes a byte array to a file.
     *
     * @param file The file to write to.
     * @param data The data to write.
     * @return True if the write was successful, false otherwise.
     */
    fun writeFile(
        file: File,
        data: ByteArray,
    ): Boolean =
        try {
            file.parentFile?.mkdirs() // Ensure the parent directory exists
            file.writeBytes(data)
            true
        } catch (e: IOException) {
            Slog.e(TAG, "writeFile failed: ${e.message}")
            false
        }

    /**
     * Writes a string to a file using UTF-8 encoding.
     *
     * @param file The file to write to.
     * @param content The content to write.
     * @return True if the write was successful, false otherwise.
     */
    fun writeFileAsString(
        file: File,
        content: String,
    ): Boolean = writeFile(file, content.toByteArray(StandardCharsets.UTF_8))
}
