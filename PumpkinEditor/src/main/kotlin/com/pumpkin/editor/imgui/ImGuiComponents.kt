package com.pumpkin.editor.imgui

import glm.Vec4
import imgui.ImGui
import imgui.type.ImBoolean
import imgui.type.ImInt
import imgui.type.ImString
import kotlin.reflect.KMutableProperty0

fun editString(label: String, value: KMutableProperty0<String>, flags: Int = 0) {
    val imString = ImString(value(), value().length + 127)
    nEditString(label, imString, flags)
    value.set(imString.get())
}

fun nEditString(label: String, value: ImString, flags: Int = 0) {
    if (flags == 0) {
        ImGui.inputText(label, value)
    } else {
        ImGui.inputText(label, value, flags)
    }
}

fun editBoolean(label: String, value: KMutableProperty0<Boolean>) {
    val imBoolean = ImBoolean(value())
    nEditBoolean(label, imBoolean)
    value.set(imBoolean.get())
}

fun nEditBoolean(label: String, value: ImBoolean) {
    ImGui.checkbox(label, value)
}

fun editFloat(label: String, value: KMutableProperty0<Float>, speed: Float = 0.01f, min: Float = 0f, max: Float = 0f, format: String? = null, flags: Int = 0) {
    val array = floatArrayOf(value())
    nEditFloat(label, array, speed, min, max, format, flags)
    value.set(array[0])
}

fun nEditFloat(label: String, value: FloatArray, speed: Float = 0.01f, min: Float = 0f, max: Float = 0f, format: String? = null, flags: Int = 0) {
    if (flags == 0) {
        if (format == null) {
            if (max <= min) {
                if (speed == 0.01f) {
                    ImGui.dragFloat(label, value)
                } else {
                    ImGui.dragFloat(label, value, speed)
                }
            } else {
                ImGui.dragFloat(label, value, speed, min, max)
            }
        } else {
            ImGui.dragFloat(label, value, speed, min, max, format)
        }
    } else {
        ImGui.dragFloat(label, value, speed, min, max, format, flags)
    }
}

fun editColor3(label: String, value: Vec4, colorEditFlags: Int = 0) {
    val array = floatArrayOf(value.r, value.g, value.b)
    nEditColor3(label, array, colorEditFlags)
    value.r = array[0]; value.g = array[1]; value.b = array[2]
}

fun nEditColor3(label: String, value: FloatArray, colorEditFlags: Int = 0) {
    if (colorEditFlags == 0) {
        ImGui.colorEdit3(label, value)
    } else {
        ImGui.colorEdit3(label, value, colorEditFlags)
    }
}

fun editColor4(label: String, value: Vec4, colorEditFlags: Int = 0) {
    val array = floatArrayOf(value.r, value.g, value.b, value.a)
    nEditColor4(label, array, colorEditFlags)
    value.r = array[0]; value.g = array[1]; value.b = array[2]; value.a = array[3]
}

fun nEditColor4(label: String, value: FloatArray, colorEditFlags: Int = 0) {
    if (colorEditFlags == 0) {
        ImGui.colorEdit4(label, value)
    } else {
        ImGui.colorEdit4(label, value, colorEditFlags)
    }
}

inline fun <reified E : Enum<E>> editEnum(label: String, valuePtr: KMutableProperty0<Int>, popupMaxItems: Int = 0) {
    editEnum(label, valuePtr, enumValues<E>(), popupMaxItems)
}

fun <E : Enum<E>> editEnum(label: String, valuePtr: KMutableProperty0<Int>, enumValues: Array<E>, popupMaxItems: Int = 0) {
    val imInt = ImInt(valuePtr())
    val names = Array(enumValues.size) { enumValues[it].name }
    nEditEnum(label, imInt, names, popupMaxItems)
    valuePtr.set(imInt.get())
}

fun nEditEnum(label: String, valuePtr: ImInt, values: Array<out String>, popupMaxItems: Int = 0) {
    if (popupMaxItems < 1) {
        ImGui.combo(label, valuePtr, values, values.size)
    } else {
        ImGui.combo(label, valuePtr, values, values.size, popupMaxItems)
    }
}