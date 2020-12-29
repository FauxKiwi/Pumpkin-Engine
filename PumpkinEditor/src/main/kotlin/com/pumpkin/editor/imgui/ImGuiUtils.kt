package com.pumpkin.editor.imgui

import java.io.ByteArrayOutputStream

fun loadFromResources(fileName: String): ByteArray? {
    ClassLoader.getSystemResourceAsStream(fileName).use { inputStream ->
        ByteArrayOutputStream().use { buffer ->
            val data = ByteArray(16384)
            var nRead: Int
            while (inputStream!!.read(data, 0, data.size).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            return buffer.toByteArray()
        }
    }
}