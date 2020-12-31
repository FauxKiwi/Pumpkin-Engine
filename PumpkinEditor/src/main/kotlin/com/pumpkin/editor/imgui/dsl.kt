package com.pumpkin.editor.imgui

import imgui.ImGui
import imgui.type.ImBoolean

@Suppress("NOTHING_TO_INLINE")
inline fun withStyleVar(styleVar: Int, value: Float, body: () -> Unit) {
    try {
        ImGui.pushStyleVar(styleVar, value)
        body()
    } finally {
        ImGui.popStyleVar()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun withStyleVar(styleVar: Int, valX: Float, valY: Float, body: () -> Unit) {
    try {
        ImGui.pushStyleVar(styleVar, valX, valY)
        body()
    } finally {
        ImGui.popStyleVar()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun withItemWidth(itemWidth: Float, body: () -> Unit) {
    try {
        ImGui.pushItemWidth(itemWidth)
        body()
    } finally {
        ImGui.popItemWidth()
    }
}


@Suppress("NOTHING_TO_INLINE")
inline fun ImGuiWindow(title: String, open: ImBoolean? = null, windowFlags: Int = 0, body: () -> Unit) {
    if (open?.get() == false) return
    try {
        ImGui.begin(title, open ?: ImBoolean(true), windowFlags)
        body()
    } finally {
        ImGui.end()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ImGuiPopup(strId: String, windowFlags: Int = 0, body: () -> Unit) {
    try {
        if (ImGui.beginPopup(strId, windowFlags))
            body()
    } finally {
        ImGui.endPopup()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ImGuiPopupContextWindow(popupFlags: Int = 0, body: () -> Unit) {
    try {
        if (ImGui.beginPopupContextWindow(popupFlags))
            body()
    } finally {
        ImGui.endPopup()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ImGuiPopupContextWindow(strId: String, popupFlags: Int = 0, body: () -> Unit) {
    try {
        if (ImGui.beginPopupContextWindow(strId, popupFlags))
            body()
    } finally {
        ImGui.endPopup()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ImGuiPopupContextItem(popupFlags: Int = 0, body: () -> Unit) {
    try {
        if (ImGui.beginPopupContextItem(popupFlags))
            body()
    } finally {
        ImGui.endPopup()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ImGuiPopupContextItem(strId: String, popupFlags: Int = 0, body: () -> Unit) {
    try {
        if (ImGui.beginPopupContextItem(strId, popupFlags))
            body()
    } finally {
        ImGui.endPopup()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun ImGuiMenuItem(label: String, shortcut: String? = null, selected: ImBoolean? = null, enabled: Boolean = true, body: () -> Unit) {
    if (enabled) {
        if (selected == null) {
            if (shortcut == null) {
                if (ImGui.menuItem(label))
                    body()
            } else {
                if (ImGui.menuItem(label, shortcut))
                    body()
            }
        } else {
            if (ImGui.menuItem(label, shortcut, selected))
                body()
        }
    } else {
        if (ImGui.menuItem(label, shortcut, selected, enabled))
            body()
    }
}