package com.pumpkin.imgui

import com.pumpkin.core.scene.TransformComponent
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import glm_.vec4.swizzle.xy
import imgui.Col
import imgui.ImGui
import imgui.StyleVar
import imgui.WindowFlag
import imgui.classes.DrawList
import kotlin.math.*

///////////////////////////////////////////////////////////////////////////
// WARNING!!!:
// THIS IS VERY TEMPORARY AND DOESN'T WORK!
///////////////////////////////////////////////////////////////////////////

object ImGuizmo {
    enum class OPERATION {
        NONE, TRANSLATE, ROTATE, SCALE
    }

    enum class MODE {
        LOCAL
    }

    enum class MOVETYPE {
        NONE, X, Y, Z, XY, YZ, XZ, XYZ
    }

    private val pos0 = Vec4(0f, 0f, 0f, 1f)
    private val unaryDir = arrayOf(Vec4(1f, 0f, 0f, 0f), Vec4(0f, 1f, 0f, 0f), Vec4(0f, 0f, 1f, 0f))
    private val gizmoSize = 50f
    private val dirCols = intArrayOf(0xff261acc.toInt(), 0xff33b333.toInt(), 0xffcc401a.toInt())
    private val circleCol = -1
    private val overCol = 0xff1a80cc.toInt()

    private lateinit var drawList: DrawList
    private var xmin = 0f; private var xmax = 0f; private var ymin = 0f; private var ymax = 0f
    private var halfDisplay = Vec2(); private var mid = Vec2()

    private var viewProj = Mat4()
    private var transformedPos0 = Vec4()
    private var gizmoPos = Vec2()
    private var vx = Vec2(); private var vy = Vec2(); private var vz = Vec2()

    private var over = false
    private var overOperation = OPERATION.NONE
    private var overMovetype = MOVETYPE.NONE
    private var using = false

    private var translatePos0 = Vec2(); private var showTranslatePath = false
    private var scaleSizeM = floatArrayOf(1f, 1f, 1f)

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
        halfDisplay = Vec2(width / 2f, height / 2f)
        mid = Vec2(x, y) + halfDisplay
    }

    fun manipulate(
        viewProjection: Mat4,
        operation: OPERATION,
        mode: MODE,
        transform: TransformComponent,
        deltaMatrix: Mat4?,
        snap: FloatArray?
    ) {
        viewProj = viewProjection //projection * glm.inverse(view)
        transformedPos0 = Mat4(transform.transform.array) * pos0
        calcGizmoPos()
        calcDirVecs()
        calcOver(operation)

        handleOperation(transform)

        when (operation) {
            OPERATION.TRANSLATE -> drawTranslationGizmo()
            OPERATION.ROTATE -> drawRotationGizmo()
            OPERATION.SCALE -> drawScaleGizmo()
        }
    }

    private fun calcGizmoPos() {
        gizmoPos = (viewProj * transformedPos0).let { it.xy * Vec2(1f, -1f) / it.w } * halfDisplay + mid
    }

    private fun calcDirVecs() {
        val hdl = halfDisplay.length()
        vx = (viewProj * unaryDir[0]).let { it.xy * Vec2(1f, -1f) } * halfDisplay / hdl
        vy = (viewProj * unaryDir[1]).let { it.xy * Vec2(1f, -1f) } * halfDisplay / hdl
        vz = (viewProj * unaryDir[2]).let { it.xy * Vec2(1f, -1f) } * halfDisplay / hdl
    }

    fun drawTranslationGizmo() {
        val over = this.over && overOperation == OPERATION.TRANSLATE
        if (showTranslatePath) {
            drawList.addCircle(translatePos0, 5f, circleCol - 0x7f000000)
            drawList.addLine(translatePos0 + glm_.glm.normalize(gizmoPos - translatePos0) * 5f, gizmoPos, circleCol - 0x7f000000, 2f)
        }
        drawRect(gizmoPos, vy, vz, gizmoSize, dirCols[0] - 0x7f000000)
        drawRect(gizmoPos, vx, vz, gizmoSize, dirCols[1] - 0x7f000000)
        drawRect(gizmoPos, vx, vy, gizmoSize, dirCols[2] - 0x7f000000)
        drawArrow(gizmoPos, vx, gizmoSize, if (over && overMovetype == MOVETYPE.X) overCol else dirCols[0], 3f)
        drawArrow(gizmoPos, vy, gizmoSize, if (over && overMovetype == MOVETYPE.Y) overCol else dirCols[1], 3f)
        drawArrow(gizmoPos, vz, gizmoSize, if (over && overMovetype == MOVETYPE.Z) overCol else dirCols[2], 3f)
        drawList.addCircleFilled(gizmoPos, 5f, if (over && overMovetype == MOVETYPE.XYZ) overCol else circleCol)
    }

    fun drawScaleGizmo() {
        val over = this.over && overOperation == OPERATION.SCALE
        drawCArrow(gizmoPos, vx, gizmoSize * scaleSizeM[0], if (over && overMovetype == MOVETYPE.X) overCol else dirCols[0], 3f)
        drawCArrow(gizmoPos, vy, gizmoSize * scaleSizeM[1], if (over && overMovetype == MOVETYPE.Y) overCol else dirCols[1], 3f)
        drawCArrow(gizmoPos, vz, gizmoSize * scaleSizeM[2], if (over && overMovetype == MOVETYPE.Z) overCol else dirCols[2], 3f)
        drawList.addCircleFilled(gizmoPos, 5f, if (over && overMovetype == MOVETYPE.XYZ) overCol else circleCol)
    }

    fun drawRotationGizmo() {
        val over = this.over && overOperation == OPERATION.ROTATE
        drawECircle(gizmoPos, vz, vy, gizmoSize, if (over && overMovetype == MOVETYPE.X) overCol else dirCols[0], 2f)
        drawECircle(gizmoPos, vx, vz, gizmoSize, if (over && overMovetype == MOVETYPE.Y) overCol else dirCols[1], 2f)
        drawECircle(gizmoPos, -vx, vy, gizmoSize, if (over && overMovetype == MOVETYPE.Z) overCol else dirCols[2], 2f)
        //drawList.addCircle(gizmoPos, gizmoSize * max(abs(vx.x), abs(vz.y)), circleCol, 30, 2f)
    }

    private var mouseDelta = Vec2(); private var isUsing = false; private var storedTransform = TransformComponent(FloatArray(9))
    private fun handleOperation(transform: TransformComponent) {
        if (!using && !isUsing) return
        if (isUsing && !ImGui.io.mouseDown[0]) { // END
            isUsing = false
            using = false

            showTranslatePath = false
            scaleSizeM = floatArrayOf(1f, 1f, 1f)

            return
        }
        if (!isUsing) { // START
            isUsing = true
            mouseDelta = Vec2()
            transform.t.copyInto(storedTransform.t)

            translatePos0 = gizmoPos
            showTranslatePath = true
        }
        mouseDelta plusAssign ImGui.io.mouseDelta

        when (overOperation) {
            OPERATION.TRANSLATE -> {
                when (overMovetype) {
                    MOVETYPE.X -> {
                        transform.position = storedTransform.position + glm.Vec3(
                            4f * mouseDelta.abs(vx) / ymax,
                            0f, 0f)
                    }
                    MOVETYPE.Y -> {
                        transform.position = storedTransform.position + glm.Vec3(0f,
                            4f * mouseDelta.abs(vy) / ymax,
                            0f)
                    }
                    MOVETYPE.Z -> {
                        transform.position = storedTransform.position + glm.Vec3(0f, 0f,
                            4f * mouseDelta.abs(vz) / ymax)
                    }
                }
            }
            OPERATION.SCALE -> {
                when (overMovetype) {
                    MOVETYPE.XYZ -> {
                        transform.scale = storedTransform.scale * (4f * mouseDelta.x / ymax).let {
                            if (it < 0) -1 / (it-1) else it + 1f
                        }.also { scaleSizeM[0] = it; scaleSizeM[1] = it; scaleSizeM[2] = it;  }
                    }
                    MOVETYPE.X -> {
                        transform.scale = storedTransform.scale * glm.Vec3(
                            (1f + 4f * (mouseDelta * vx).length() / ymax).let {
                                if (mouseDelta.abs(vx) < 0) 1 / it else it
                            }.also { scaleSizeM[0] = it },
                            1f, 1f)
                    }
                    MOVETYPE.Y -> {
                        transform.scale = storedTransform.scale * glm.Vec3(1f,
                            (1f + 4f * (mouseDelta * vy).length() / ymax).let {
                                if (mouseDelta.abs(vy) < 0) 1 / it else it
                            }.also { scaleSizeM[1] = it },
                            1f)
                    }
                    MOVETYPE.Z -> {
                        transform.scale = storedTransform.scale * glm.Vec3(1f, 1f,
                            (1f + 4f * (mouseDelta * vz).length() / ymax).let {
                                if (mouseDelta.abs(vz) < 0) 1 / it else it
                            }.also { scaleSizeM[2] = it })
                    }
                }
            }
        }
    }

    private fun sgn(f: Float): Int {
        if (f == 0f) return 0
        return if (f < 0f) -1 else 1
    }

    private fun drawRect(position: Vec2, vy: Vec2, vz: Vec2, size: Float, color: Int) {
        drawList.addConvexPolyFilled(arrayListOf(
            position + (vy + vz) * size * 0.2f,
            position + vy * size * 0.6f + vz * size * 0.2f,
            position + (vy + vz) * size * 0.6f,
            position + vy * size * 0.2f + vz * size * 0.6f),
            color
        )
    }

    private fun drawArrow(position: Vec2, dir: Vec2, length: Float, color: Int, thickness: Float) {
        drawList.addLine(position, position + dir * length, color, thickness)
        drawList.addTriangleFilled(
            position + dir * length + glm_.glm.normalize(dir) * (4 * thickness),
            position + dir * (length - thickness) + Vec2(-dir.y, dir.x).normalizeAssign() * thickness * 2,
            position + dir * (length - thickness) + Vec2(dir.y, -dir.x).normalizeAssign() * thickness * 2,
            color
        )
    }

    private fun drawCArrow(position: Vec2, dir: Vec2, length: Float, color: Int, thickness: Float) {
        drawList.addLine(position, position + dir * (length + 1.5f * thickness), color, thickness)
        drawList.addCircleFilled(position + dir * (length + 1.5f * thickness), 1.5f * thickness, color, 15)
    }

    private fun drawECircle(position: Vec2, right: Vec2, up: Vec2, size: Float, color: Int, thickness: Float) {
        val segments = 30f//(right.length() + up.length()) * 0.5f
        val points = mutableListOf<Vec2>()
        for (i in 0 until segments.toInt()) {
            val rad = (2 * PI * i) / segments
            points.add(position + right * cos(rad) * size + up * sin(rad) * size)
        }
        drawList.addPolyline(points, color, true, thickness)
    }

    private fun calcOver(operation: OPERATION) {
        if (isUsing) return

        over = when (operation) {
            OPERATION.TRANSLATE, OPERATION.SCALE -> calcOverTranslateOrScale()
            OPERATION.ROTATE -> calcOverRotate()
            else -> false
        }
        if (over) {
            overOperation = operation
            if (ImGui.io.mouseClicked[0]) {
                ImGui.io.wantCaptureMouse = true
                using = true
            }
        }
    }

    private fun calcOverTranslateOrScale(): Boolean {
        val mousePos = ImGui.io.mousePos

        if (mousePos.inCircle(gizmoPos, 5f)) {
            overMovetype = MOVETYPE.XYZ
            return true
        }
        if (mousePos.inVec(gizmoPos, gizmoPos + vx * gizmoSize) || mousePos.inCircle(gizmoPos + vx * (gizmoSize + 3f), 2f)) {
            overMovetype = MOVETYPE.X
            return true
        }
        if (mousePos.inVec(gizmoPos, gizmoPos + vy * gizmoSize) || mousePos.inCircle(gizmoPos + vy * (gizmoSize + 3f), 2f)) {
            overMovetype = MOVETYPE.Y
            return true
        }
        if (mousePos.inVec(gizmoPos, gizmoPos + vx * gizmoSize) || mousePos.inCircle(gizmoPos + vz * (gizmoSize + 3f), 2f)) {
            overMovetype = MOVETYPE.Z
            return true
        }
        return false
    }

    private fun calcOverRotate(): Boolean {
        //TODO: Work
        val mousePos = ImGui.io.mousePos

        if (mousePos.inECircle(gizmoPos, vz, vy)) {
            overMovetype = MOVETYPE.X
            return true
        }
        if (mousePos.inECircle(gizmoPos, vx, vz)) {
            overMovetype = MOVETYPE.Y
            return true
        }
        if (mousePos.inECircle(gizmoPos, -vx, vy)) {
            overMovetype = MOVETYPE.Z
            return true
        }
        return false
    }

    private fun Vec2.abs(dir: Vec2): Float = this.dot(dir) / dir.length()

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Vec2.absToNormalized(dir: Vec2): Float = this.dot(dir)

    private fun Vec2.inVec(pos1: Vec2, pos2: Vec2): Boolean {
        val dir = (pos2 - pos1).normalizeAssign()
        val rpos = this - pos1
        return ((dir * rpos.x).y in rpos.y - 2f .. rpos.y + 2f)
    }

    private fun Vec2.inCircle(pos: Vec2, r: Float): Boolean {
        return (this - pos).length() <= (r + 2f)
    }

    private fun Vec2.inECircle(pos: Vec2, r: Vec2, u: Vec2): Boolean {
        val rpos = (this - pos).negateAssign()
        return acos(rpos.x) in asin(rpos.y) - 2f .. asin(rpos.y) + 2f
    }

    fun isOver(): Boolean = over

    fun isUsing(): Boolean = using

    fun decomposeFromMatrix(matrix: Mat4, translation: Vec3, rotation: Vec3, scale: Vec3) {
        //TODO("Not yet implemented")
    }
}