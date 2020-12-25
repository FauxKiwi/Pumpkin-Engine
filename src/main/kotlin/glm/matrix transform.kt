package glm

fun translate(v: Vec3) = Mat4(
    glm_.glm.translate(glm_.mat4x4.Mat4.identity, v.x, v.y, v.z).array
) //TODO

fun translate(x: Float, y: Float, z: Float) = Mat4(
    glm_.glm.translate(glm_.mat4x4.Mat4.identity, x, y, z).array
) //TODO

fun rotate(angle: Float, axis: Vec3) = Mat4(
    glm_.glm.rotate(glm_.mat4x4.Mat4.identity, angle, axis.x, axis.y, axis.z).array
) //TODO

fun rotateXYZ(v: Vec3) = Mat4(
    glm_.glm.rotateXYZ(glm_.mat4x4.Mat4.identity, v.x, v.y, v.z).array
) //TODO

fun rotateXYZ(x: Float, y: Float, z: Float) = Mat4(
    glm_.glm.rotateXYZ(glm_.mat4x4.Mat4.identity, x, y, z).array
) //TODO

fun scale(v: Vec3) = Mat4(
    glm_.glm.scale(glm_.mat4x4.Mat4.identity, v.x, v.y, v.z).array
) //TODO

fun scale(x: Float, y: Float, z: Float) = Mat4(
    glm_.glm.scale(glm_.mat4x4.Mat4.identity, x, y, z).array
) //TODO