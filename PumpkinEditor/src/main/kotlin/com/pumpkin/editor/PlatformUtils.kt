package com.pumpkin.editor

import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs

object FileDialog {
    fun open(title: String = "Open", filter: FileFilter? = null): String? = MemoryStack.stackPush().use { stack ->
        val filterPatterns = filter?.types?.let { filterTypesToPointerBuffer(stack, it) }
        return TinyFileDialogs.tinyfd_openFileDialog(title, null, filterPatterns, filter?.description, false)
    }

    fun save(title: String = "Save", filter: FileFilter? = null): String? = MemoryStack.stackPush().use { stack ->
        val filterPatterns = filter?.types?.let { filterTypesToPointerBuffer(stack, it) }
        return TinyFileDialogs.tinyfd_saveFileDialog(title, null, filterPatterns, filter?.description)
    }

    private fun filterTypesToPointerBuffer(stack: MemoryStack, filters: Array<out String>): PointerBuffer {
        val filterPatterns: PointerBuffer = stack.mallocPointer(filters.size)
        filters.forEach {
            filterPatterns.put(stack.UTF8("*.$it"))
        }
        filterPatterns.flip()
        return filterPatterns
    }

    class FileFilter(val description: String, vararg val types: String)
}
