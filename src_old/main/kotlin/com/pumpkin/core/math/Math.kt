package com.pumpkin.core.math

import glm.Mat4
import glm.Vec3

fun transform(position: Vec3, rotation: Vec3, scale: Vec3): Mat4 =
    glm.translate(position) *
    glm.rotateXYZ(rotation.x, rotation.y, rotation.z) *
    glm.scale(scale)

fun transform(t: FloatArray): Mat4 =
    glm.translate(t[0], t[1], t[2]) *
            glm.rotateXYZ(t[3], t[4], t[5]) *
            glm.scale(t[6], t[7], t[8])

fun inverseTransform(position: Vec3, rotation: Vec3, scale: Vec3): Mat4 =
    glm.scale(inverseScale(scale)) *
    glm.rotateXYZ(-(rotation.x), -(rotation.y), -(rotation.z)) *
    glm.translate(-position)

fun inverseTransform(t: FloatArray): Mat4 =
    glm.scale(1 / t[6], 1 / t[7], 1 / t[8]) *
            glm.rotateXYZ(-t[3], -t[4], -t[5]) *
            glm.translate(-t[0], -t[1], -t[2])

fun inverseScale(scale: Vec3): Vec3 = Vec3(1 / scale.x, 1 / scale.y, 1 / scale.z)