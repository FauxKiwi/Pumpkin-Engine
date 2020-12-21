package com.pumpkin.core.imgui

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

///////////////////////////////////////////////////////////////////////////
// WARNING!!!:
// THIS IS VERY TEMPORARY AND DOESN'T WORK!
///////////////////////////////////////////////////////////////////////////

object ImGuizmo {
    enum class OPERATION {
        ROTATE
    }

    enum class MODE {
        LOCAL
    }

    fun setOrthographic(orthographic: Boolean) {
        //TODO("Not yet implemented")
    }

    fun setDrawlist() {
        //TODO("Not yet implemented")
    }

    fun setRect(x: Float, y: Float, width: Float, height: Float) {
        //TODO("Not yet implemented")
    }

    fun manipulate(
        view: Mat4,
        projection: Mat4,
        operation: OPERATION,
        mode: MODE,
        transform: Mat4,
        deltaMatrix: Mat4?,
        snap: FloatArray?
    ) {
        //TODO("Not yet implemented")
    }

    fun isUsing(): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    fun decomposeFromMatrix(matrix: Mat4, translation: Vec3, rotation: Vec3, scale: Vec3) {
        //TODO("Not yet implemented")
    }
}