package org.lineageos.samsungparts.udfps

/**
 * Interface for reading and writing to SysFS nodes for biometric sensors.
 */
interface SemBioSysFsProvider {
    /**
     * Reads data from the specified SysFS path.
     *
     * @param path The SysFS path to read from.
     * @return The data read as a byte array, or null if an error occurs.
     */
    fun readSysFs(path: String): ByteArray?

    /**
     * Writes data to the specified SysFS path.
     *
     * @param path The SysFS path to write to.
     * @param data The data to write as a byte array.
     */
    fun writeSysFs(path: String, data: ByteArray)
}
