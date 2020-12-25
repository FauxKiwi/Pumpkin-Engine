package glm

fun rotate(q: Quat, v: Vec3): Vec3 {
    val r = glm_.glm.rotate(glm_.quat.Quat(q.w, q.x, q.y, q.z), glm_.vec3.Vec3(v.x, v.y, v.z))
    return Vec3(
        r.x,
        r.y,
        r.z
    )
} //TODO