package glm

fun perspective(fovY: Float, aspectRatio: Float, zNear: Float, zFar: Float) = Mat4(
    glm_.glm.perspective(fovY, aspectRatio, zNear, zFar).array
) //TODO

fun ortho(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float) = Mat4(
    glm_.glm.ortho(left, right, bottom ,top, zNear, zFar).array
) //TODO