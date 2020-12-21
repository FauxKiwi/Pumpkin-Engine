package com.pumpkin.core.imgui

import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec2.swizzle.yx
import glm_.vec3.Vec3
import glm_.vec3.swizzle.xy
import glm_.vec4.Vec4
import glm_.vec4.swizzle.xy
import imgui.Col
import imgui.ImGui
import imgui.StyleVar
import imgui.WindowFlag
import imgui.classes.DrawList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

///////////////////////////////////////////////////////////////////////////
// WARNING!!!:
// THIS IS VERY TEMPORARY AND DOESN'T WORK!
///////////////////////////////////////////////////////////////////////////

object ImGuizmo {
    enum class OPERATION {
        TRANSLATE, ROTATE, SCALE
    }

    enum class MODE {
        LOCAL
    }

    private val unaryDir = arrayOf(Vec3(1f, 0f, 0f), Vec3(0f, 1f, 0f), Vec3(0f, 0f, 1f))
    private val dirCols = intArrayOf(0xff261acc.toInt(), 0xff33b333.toInt(), 0xffcc401a.toInt())
    private var circleCol = 0xffccffff.toInt()

    private lateinit var drawList: DrawList
    private var xmin = 0f; private var xmax = 0f; private var ymin = 0f; private var ymax = 0f
    private var display = Vec2(); private var mid = Vec2()

    private var using = false

    fun setOrthographic(orthographic: Boolean) {
        //TODO("Not yet implemented")
    }

    fun setDrawlist() {
        drawList = ImGui.windowDrawList
    }

    val windowFlags = WindowFlag.NoTitleBar.i or WindowFlag.NoResize.i or WindowFlag.NoScrollbar.i or WindowFlag.NoInputs.i or WindowFlag.NoSavedSettings.i or WindowFlag.NoFocusOnAppearing.i or WindowFlag.NoBringToFrontOnFocus.i
    fun newFrame() = with(ImGui) {
        setNextWindowSize(mainViewport.size)
        setNextWindowPos(mainViewport.pos)

        pushStyleColor(Col.WindowBg, 0)
        pushStyleColor(Col.Border, 0)
        pushStyleVar(StyleVar.WindowRounding, 0f)

        begin("gizmo", null, windowFlags)
        drawList = windowDrawList
        end()

        popStyleVar()
        popStyleColor(2)
    }

    fun setRect(x: Float, y: Float, width: Float, height: Float) {
        xmin = x
        xmax = x + width
        ymin = y
        ymax = y + height
        display = Vec2(width, height)
        mid = Vec2(x, y) + display * 0.5f
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
        when (operation) {
            OPERATION.TRANSLATE -> drawTranslationGizmo(getGizmoPos(view, projection, transform), getDirVecs(view, projection, transform))
            OPERATION.ROTATE -> drawRotationGizmo(getGizmoPos(view, projection, transform), getDirVecs(view, projection, transform))
            OPERATION.SCALE -> drawScaleGizmo(getGizmoPos(view, projection, transform), getDirVecs(view, projection, transform))
        }
    }

    fun getGizmoPos(view: Mat4, projection: Mat4, transform: Mat4): Vec2 {
        return (projection * view * transform * Vec4(0f, 0f, 0f, 1f)).xy * display + mid
    }

    fun getDirVecs(view: Mat4, projection: Mat4, transform: Mat4): Triple<Vec2, Vec2, Vec2> {

        val vx: Vec2 = (projection * view * transform * Vec4(unaryDir[0], 0f)).normalizeAssign().xy * 50f
        val vy: Vec2 = (projection * view * transform * Vec4(unaryDir[1], 0f)).normalizeAssign().xy * -50f
        val vz: Vec2 = (projection * view * transform * Vec4(unaryDir[2], 0f)).normalizeAssign().xy * 50f

        return Triple(vx, vy, vz)
    }

    fun drawTranslationGizmo(position: Vec2, dirVecs: Triple<Vec2, Vec2, Vec2>) {
        val (vx, vy, vz) = dirVecs
        drawList.addLine(position + vx * 0.15f, position + vx, dirCols[0], 2.5f)
        drawList.addTriangleFilled(position + vx * 1.2f, position + vx + vx.yx * 0.1f, position + vx - vx.yx * 0.1f, dirCols[0])
        drawList.addLine(position + vy * 0.15f, position + vy, dirCols[1], 2.5f)
        drawList.addTriangleFilled(position + vy * 1.2f, position + vy + vy.yx * 0.1f, position + vy - vy.yx * 0.1f, dirCols[1])
        drawList.addLine(position + vz * 0.15f, position + vz, dirCols[2], 2.5f)
        drawList.addTriangleFilled(position + vz * 1.2f, position + vz + vz.yx * 0.1f, position + vz - vz.yx * 0.1f, dirCols[2])
        drawList.addCircleFilled(position, 5f, circleCol)
    }

    fun drawScaleGizmo(position: Vec2, dirVecs: Triple<Vec2, Vec2, Vec2>) {
        val (vx, vy, vz) = dirVecs
        drawList.addLine(position + vx * 0.15f, position + vx, dirCols[0], 2.5f)
        drawList.addCircleFilled(position + vx * 1.1f, vx.length() * 0.1f, dirCols[0])
        drawList.addLine(position + vy * 0.15f, position + vy, dirCols[1], 2.5f)
        drawList.addCircleFilled(position + vy * 1.1f, vy.length() * 0.1f, dirCols[1])
        drawList.addLine(position + vz * 0.15f, position + vz, dirCols[2], 2.5f)
        drawList.addCircleFilled(position + vz * 1.1f, vz.length() * 0.1f, dirCols[2])
        drawList.addCircleFilled(position, 5f, circleCol)
    }

    fun drawRotationGizmo(position: Vec2, dirVecs: Triple<Vec2, Vec2, Vec2>) {
        val (vx, vy, vz) = dirVecs
        drawCircle(position, vz, vy, dirCols[0], 3f)
        drawCircle(position, vx, vz, dirCols[1], 3f)
        drawCircle(position, -vx, vy, dirCols[2], 3f)
        drawList.addCircleFilled(position, 5f, circleCol)
    }

    private fun drawCircle(position: Vec2, right: Vec2, up: Vec2, color: Int, thickness: Float) {
        val segments = 50f//(right.length() + up.length()) * 0.5f
        val points = mutableListOf<Vec2>()
        for (i in 0 until segments.toInt()) {
            val rad = (2 * PI * i) / segments
            points.add(position + right * cos(rad) + up * sin(rad))
        }
        drawList.addPolyline(points, color, true, thickness)
    }

    fun isOver(): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    fun isUsing(): Boolean = using

    fun decomposeFromMatrix(matrix: Mat4, translation: Vec3, rotation: Vec3, scale: Vec3) {
        //TODO("Not yet implemented")
    }
}