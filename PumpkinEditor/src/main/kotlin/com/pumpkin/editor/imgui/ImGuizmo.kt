package com.pumpkin.editor.imgui

import com.pumpkin.core.scene.TransformComponent
import glm.Mat4
import glm.Vec2
import glm.Vec3
import glm.Vec4
import imgui.*
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImBoolean
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

    private lateinit var drawList: ImDrawList
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
        drawList = ImGui.getWindowDrawList()
    }

    val windowFlags = ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoResize or ImGuiWindowFlags.NoScrollbar or ImGuiWindowFlags.NoInputs or ImGuiWindowFlags.NoSavedSettings or ImGuiWindowFlags.NoFocusOnAppearing or ImGuiWindowFlags.NoBringToFrontOnFocus
    fun newFrame() {
        ImGui.setNextWindowSize(ImGui.getMainViewport().sizeX, ImGui.getMainViewport().sizeY)
        ImGui.setNextWindowPos(ImGui.getMainViewport().posX, ImGui.getMainViewport().posY)

        ImGui.pushStyleColor(ImGuiCol.WindowBg, 0)
        ImGui.pushStyleColor(ImGuiCol.Border, 0)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f)

        ImGui.begin("gizmo", ImBoolean(true), windowFlags)
        drawList = ImGui.getWindowDrawList()
        ImGui.end()

        ImGui.popStyleVar()
        ImGui.popStyleColor(2)
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

        //handleOperation(transform)

        when (operation) {
            OPERATION.TRANSLATE -> drawTranslationGizmo()
            OPERATION.ROTATE -> drawRotationGizmo()
            OPERATION.SCALE -> drawScaleGizmo()
            else -> return
        }
        //drawList.addLine(xmin, ymin, xmax, ymax, -1)
    }

    /*private fun calcGizmoPos() {
        gizmoPos = (viewProj * transformedPos0).let { it.xy * Vec2(1f, -1f) / it.w } * halfDisplay + mid
    }

    private fun calcDirVecs() {

    }

    private fun calcOver(operation: OPERATION) {

    }

    private fun drawTranslationGizmo() {
        drawList.addLine(gizmoPos.x, gizmoPos.y, gizmoPos.x - 10, gizmoPos.y - 10, -1)
    }

    private fun drawRotationGizmo() {

    }

    private fun drawScaleGizmo() {

    }*/


    private fun calcGizmoPos() {
        gizmoPos = (viewProj * transformedPos0).let { it.xy * Vec2(1f, -1f) / it.w } * halfDisplay + mid
    }

    private fun calcDirVecs() {
        val hdl = halfDisplay.length
        vx = (viewProj * unaryDir[0]).let { it.xy * Vec2(1f, -1f) } * halfDisplay / hdl
        vy = (viewProj * unaryDir[1]).let { it.xy * Vec2(1f, -1f) } * halfDisplay / hdl
        vz = (viewProj * unaryDir[2]).let { it.xy * Vec2(1f, -1f) } * halfDisplay / hdl
    }

    fun drawTranslationGizmo() {
        val over = over && overOperation == OPERATION.TRANSLATE
        if (showTranslatePath) {
            drawList.addCircle(translatePos0.x, translatePos0.y, /*(translatePos0,*/ 5f, circleCol - 0x7f000000)
            val linePos = translatePos0 + glm.normalize(gizmoPos - translatePos0) * 5f
            drawList.addLine(linePos.x, linePos.y, gizmoPos.x, gizmoPos.y, circleCol - 0x7f000000, 2f)
        }
        drawRect(gizmoPos, vy, vz, gizmoSize, dirCols[0] - 0x7f000000)
        drawRect(gizmoPos, vx, vz, gizmoSize, dirCols[1] - 0x7f000000)
        drawRect(gizmoPos, vx, vy, gizmoSize, dirCols[2] - 0x7f000000)
        drawArrow(gizmoPos, vx, gizmoSize, if (over && overMovetype == MOVETYPE.X) overCol else dirCols[0], 3f)
        drawArrow(gizmoPos, vy, gizmoSize, if (over && overMovetype == MOVETYPE.Y) overCol else dirCols[1], 3f)
        drawArrow(gizmoPos, vz, gizmoSize, if (over && overMovetype == MOVETYPE.Z) overCol else dirCols[2], 3f)
        drawList.addCircleFilled(gizmoPos.x, gizmoPos.y, 5f, if (over && overMovetype == MOVETYPE.XYZ) overCol else circleCol)
    }

    fun drawScaleGizmo() {
        val over = over && overOperation == OPERATION.SCALE
        drawCArrow(gizmoPos, vx, gizmoSize * scaleSizeM[0], if (over && overMovetype == MOVETYPE.X) overCol else dirCols[0], 3f)
        drawCArrow(gizmoPos, vy, gizmoSize * scaleSizeM[1], if (over && overMovetype == MOVETYPE.Y) overCol else dirCols[1], 3f)
        drawCArrow(gizmoPos, vz, gizmoSize * scaleSizeM[2], if (over && overMovetype == MOVETYPE.Z) overCol else dirCols[2], 3f)
        drawList.addCircleFilled(gizmoPos.x, gizmoPos.y, 5f, if (over && overMovetype == MOVETYPE.XYZ) overCol else circleCol)
    }

    fun drawRotationGizmo() {
        val over = over && overOperation == OPERATION.ROTATE
        drawECircle(gizmoPos, vz, vy, gizmoSize, if (over && overMovetype == MOVETYPE.X) overCol else dirCols[0], 2f)
        drawECircle(gizmoPos, vx, vz, gizmoSize, if (over && overMovetype == MOVETYPE.Y) overCol else dirCols[1], 2f)
        drawECircle(gizmoPos, -vx, vy, gizmoSize, if (over && overMovetype == MOVETYPE.Z) overCol else dirCols[2], 2f)
        //drawList.addCircle(gizmoPos, gizmoSize * max(abs(vx.x), abs(vz.y)), circleCol, 30, 2f)
    }

    private var mouseDelta = Vec2(); private var isUsing = false; private var storedTransform = TransformComponent(FloatArray(9))
    private fun handleOperation(transform: TransformComponent) {
        if (!using && !isUsing) return
        if (isUsing && !ImGui.getIO().getMouseDown(0)) { // END
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
        val mdiv2 = ImVec2()
        ImGui.getIO().getMouseDelta(mdiv2)
        mouseDelta plusAssign Vec2(mdiv2.x, mdiv2.y)

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
                            4f * mouseDelta.abs(vz) / ymax
                        )
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
                            (1f + 4f * (mouseDelta * vx).length / ymax).let {
                                if (mouseDelta.abs(vx) < 0) 1 / it else it
                            }.also { scaleSizeM[0] = it },
                            1f, 1f)
                    }
                    MOVETYPE.Y -> {
                        transform.scale = storedTransform.scale * glm.Vec3(1f,
                            (1f + 4f * (mouseDelta * vy).length / ymax).let {
                                if (mouseDelta.abs(vy) < 0) 1 / it else it
                            }.also { scaleSizeM[1] = it },
                            1f)
                    }
                    MOVETYPE.Z -> {
                        transform.scale = storedTransform.scale * glm.Vec3(1f, 1f,
                            (1f + 4f * (mouseDelta * vz).length / ymax).let {
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
        val vec2array = arrayOf(
            position + (vy + vz) * size * 0.2f,
            position + vy * size * 0.6f + vz * size * 0.2f,
            position + (vy + vz) * size * 0.6f,
            position + vy * size * 0.2f + vz * size * 0.6f)
        drawList.addConvexPolyFilled(
            arrayOf(
                ImVec2(vec2array[0].x, vec2array[0].y),
                ImVec2(vec2array[1].x, vec2array[1].y),
                ImVec2(vec2array[2].x, vec2array[2].y),
                ImVec2(vec2array[3].x, vec2array[3].y)
            ),
            vec2array.size,
            color
        )
    }

    private fun drawArrow(position: Vec2, dir: Vec2, length: Float, color: Int, thickness: Float) {
        val p2 = position + dir * length
        drawList.addLine(position.x, position.y, p2.x, p2.y, color, thickness)
        val tp1 = position + dir * length + glm.normalize(dir) * (4 * thickness)
        val tp2 = position + dir * (length - thickness) + Vec2(-dir.y, dir.x).normalizeAssign() * thickness * 2f
        val tp3 = position + dir * (length - thickness) + Vec2(dir.y, -dir.x).normalizeAssign() * thickness * 2f
        drawList.addTriangleFilled(tp1.x, tp1.y, tp2.x, tp2.y, tp3.x, tp3.y, color)
    }

    private fun drawCArrow(position: Vec2, dir: Vec2, length: Float, color: Int, thickness: Float) {
        val p2 = position + dir * (length + 1.5f * thickness)
        drawList.addLine(position.x, position.y, p2.x, p2.y, color, thickness)
        val c = position + dir * (length + 1.5f * thickness)
        drawList.addCircleFilled(c.x, c.y, 1.5f * thickness, color, 15)
    }

    private fun drawECircle(position: Vec2, right: Vec2, up: Vec2, size: Float, color: Int, thickness: Float) {
        val segments = 30f//(right.length() + up.length()) * 0.5f
        val points = Array<ImVec2?>(segments.toInt()) { null }
        for (i in 0 until segments.toInt()) {
            val rad = ((2 * PI * i) / segments).toFloat()
            val point = (position + right * glm.cos(rad) * size + up * glm.sin(rad) * size)
            points[i] = ImVec2(point.x, point.y)
        }
        drawList.addPolyline(points, segments.toInt(), color, true, thickness)
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
            if (ImGui.getIO().getMouseDown(0)) {
                ImGui.getIO().wantCaptureMouse = true
                using = true
            }
        }
    }

    private fun calcOverTranslateOrScale(): Boolean {
        val mousePosI = ImVec2()
        ImGui.getIO().getMousePos(mousePosI)
        val mousePos = Vec2(mousePosI.x, mousePosI.y)

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
        val mousePosI = ImVec2()
        ImGui.getIO().getMousePos(mousePosI)
        val mousePos = Vec2(mousePosI.x, mousePosI.y)

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

    private fun Vec2.abs(dir: Vec2): Float = this.dot(dir) / dir.length

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Vec2.absToNormalized(dir: Vec2): Float = this.dot(dir)

    private fun Vec2.inVec(pos1: Vec2, pos2: Vec2): Boolean {
        val dir = (pos2 - pos1).normalizeAssign()
        val rpos = this - pos1
        return ((dir * rpos.x).y in rpos.y - 2f .. rpos.y + 2f)
    }

    private fun Vec2.inCircle(pos: Vec2, r: Float): Boolean {
        return (this - pos).length <= (r + 2f)
    }

    private fun Vec2.inECircle(pos: Vec2, r: Vec2, u: Vec2): Boolean {
        val rpos = (this - pos).negateAssign()
        return acos(rpos.x) in asin(rpos.y) - 2f .. asin(rpos.y) + 2f
    }

    //fun isOver(): Boolean = over

    //fun isUsing(): Boolean = using

    fun decomposeFromMatrix(matrix: Mat4, translation: Vec3, rotation: Vec3, scale: Vec3) {
        //TODO("Not yet implemented")
    }
}